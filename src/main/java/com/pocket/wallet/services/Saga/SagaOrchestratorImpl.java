package com.pocket.wallet.services.Saga;

import java.util.List;

import org.springframework.stereotype.Service;

import tools.jackson.databind.json.JsonMapper;
import com.pocket.wallet.entities.SagaInstance;
import com.pocket.wallet.entities.SagaStatus;
import com.pocket.wallet.entities.SagaStep;
import com.pocket.wallet.entities.StepStatus;
import com.pocket.wallet.repositories.SagaInstanceRepository;
import com.pocket.wallet.repositories.SagaStepRepository;
import com.pocket.wallet.services.Saga.Step.SagaStepFactory;

import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@AllArgsConstructor
@Slf4j
public class SagaOrchestratorImpl implements SagaOrchestrator{

    private final JsonMapper jsonMapper;
    private final SagaInstanceRepository sagaInstanceRepository;
    private final SagaStepFactory sagaStepFactory;
    private final SagaStepRepository sagaStepRepository;

    @Override
    @Transactional
    public Long startSaga(SagaContext context) {
        try {
            String contextJson = jsonMapper.writeValueAsString(context);

            SagaInstance sagaInstance = SagaInstance.builder()
                                            .context(contextJson)
                                            .sagaStatus(SagaStatus.INITIATED)
                                            .build();

            sagaInstance = sagaInstanceRepository.save(sagaInstance);

            log.info("started saga with id {}", sagaInstance.getId());

            return sagaInstance.getId();

        } catch (Exception e) {
            log.error("Error starting saga");
            throw new RuntimeException("Error starting saga", e);
        }
    }

    @Override
    @Transactional
    public boolean executeStep(Long sagaInstanceId, String stepName) {

        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId)
                                    .orElseThrow(()-> new RuntimeException("no instance with this id found"));

        SagaStepInterface step = sagaStepFactory.getSagaStep(stepName);

        if(step == null){
            log.error("saga step not found for step name {}", stepName);
            throw new RuntimeException("Saga step not found");
        }

        // SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId, StepStatus.PENDING)
        //                         .stream()
        //                         .filter(s-> s.getStepName().equals(stepName))
        //                         .findFirst()
        //                         .orElse(SagaStep.builder().sagaInstanceId(sagaInstanceId).stepName(stepName)
        //                                 .status(StepStatus.PENDING).build());

        SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId, stepName, StepStatus.PENDING)
                                .orElse(
                                    SagaStep.builder().sagaInstanceId(sagaInstanceId).stepName(stepName)
                                         .status(StepStatus.PENDING).build()
                                );

        if(sagaStepDB.getId() == null){
            sagaStepDB = sagaStepRepository.save(sagaStepDB);
        }

        try {
            SagaContext sagaContext = jsonMapper.readValue(sagaInstance.getContext(), SagaContext.class); 
            sagaStepDB.setStatus(StepStatus.RUNNING);
            sagaStepRepository.save(sagaStepDB);

            boolean suuccess = step.execute(sagaContext);

            if(suuccess){
                sagaStepDB.setStatus(StepStatus.COMPLETED);    
                sagaStepRepository.save(sagaStepDB);            //updating the status to completed in db 

                sagaInstance.setCurrentStep(stepName);          //step we just completed

                sagaInstance.setSagaStatus(SagaStatus.RUNNING);
                sagaInstanceRepository.save(sagaInstance);

                log.info("Step executed successfully");
                return true;    
            }else{
                sagaStepDB.setStatus(StepStatus.FAILED);
                sagaStepRepository.save(sagaStepDB);
                log.error("Step {} faild", stepName);
                return false;
            }


        } catch (Exception e) {
            sagaStepDB.setStatus(StepStatus.FAILED);
            sagaStepRepository.save(sagaStepDB);
            log.error("Step {} faild", stepName);
            return false;
        }

    }

    @Override
    @Transactional
    public boolean compensateStep(Long sagaInstanceId, String stepName) {

          SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId)
                                    .orElseThrow(()-> new RuntimeException("no instance with this id found"));

        SagaStepInterface step = sagaStepFactory.getSagaStep(stepName);

        if(step == null){
            log.error("saga step not found for step name {}", stepName);
            throw new RuntimeException("Saga step not found");
        }

        // SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStatus(sagaInstanceId, StepStatus.PENDING)
        //                         .stream()
        //                         .filter(s-> s.getStepName().equals(stepName))
        //                         .findFirst()
        //                         .orElse(SagaStep.builder().sagaInstanceId(sagaInstanceId).stepName(stepName)
        //                                 .status(StepStatus.PENDING).build());

        SagaStep sagaStepDB = sagaStepRepository.findBySagaInstanceIdAndStepNameAndStatus(sagaInstanceId, stepName, StepStatus.COMPLETED)
                                .orElse(
                                   null
                                );

        if(sagaStepDB.getId() == null){
            log.error("Step {} not found in the db for saga instance {}, so it is already compensated or not executed", stepName, sagaInstanceId);
            return true;
        }

        try {
            SagaContext sagaContext = jsonMapper.readValue(sagaInstance.getContext(), SagaContext.class); 
            sagaStepDB.setStatus(StepStatus.COMPENSATING);
            sagaStepRepository.save(sagaStepDB);

            boolean suuccess = step.compensate(sagaContext);

            if(suuccess){
                sagaStepDB.setStatus(StepStatus.COMPENSATED);    
                sagaStepRepository.save(sagaStepDB);            //updating the status to completed in db 

                log.info("Step compensated successfully");
                return true;    
            }else{
                sagaStepDB.setStatus(StepStatus.FAILED);
                sagaStepRepository.save(sagaStepDB);
                log.error("Step {} faild", stepName);
                return false;
            }


        } catch (Exception e) {
            sagaStepDB.setStatus(StepStatus.FAILED);
            sagaStepRepository.save(sagaStepDB);
            log.error("Step {} faild", stepName);
            return false;
        }
                    

    }

    @Override
    public SagaInstance getSagaInstance(Long sagaInstanceId) {
        return sagaInstanceRepository.findById(sagaInstanceId)
                .orElseThrow(()-> new RuntimeException("No saga step instance with this id found"));
    }

    @Override
    @Transactional
    public void failSaga(Long sagaInstanceId) {
        SagaInstance sagaInstance =  sagaInstanceRepository.findById(sagaInstanceId)
        .orElseThrow(()-> new RuntimeException("No saga step instance with this id found"));

        sagaInstance.setSagaStatus(SagaStatus.FAILD);
        compensateSaga(sagaInstanceId);
        sagaInstanceRepository.save(sagaInstance);
    }

    @Override
    @Transactional
    public void compensateSaga(Long sagaInstanceId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId)
        .orElseThrow(()-> new RuntimeException("No saga step instance with this id found"));

        sagaInstance.setSagaStatus(SagaStatus.COMPENSATING);
        sagaInstanceRepository.save(sagaInstance);
        // get the all the steps of the instance having steps as completed of this stepname.

        List<SagaStep> sagaSteps = sagaStepRepository.findCompletedSagaStepBySagaInstanceId(sagaInstanceId);

        boolean allCompensated = true;
        for(SagaStep completedStep : sagaSteps){
            boolean compensated = this.compensateStep(sagaInstanceId, completedStep.getStepName());

            if(!compensated){
                allCompensated = false;
            }
        }

        if(allCompensated){
            sagaInstance.setSagaStatus(SagaStatus.COMPENSATED);
            sagaInstanceRepository.save(sagaInstance);
            log.info("saga id {} compensated successfully", sagaInstanceId);
        }else{
            log.error("unable to successfully compensate saga id {}", sagaInstanceId);
        }


    }

    @Override
    @Transactional
    public void completeSaga(Long sagaInstanceId) {
        SagaInstance sagaInstance = sagaInstanceRepository.findById(sagaInstanceId)
        .orElseThrow(()-> new RuntimeException("No saga step instance with this id found"));

        sagaInstance.setSagaStatus(SagaStatus.COMPLETED);

        sagaInstanceRepository.save(sagaInstance);
    }
    
}
