package com.crpdev.io.paymentsssm.config;

import com.crpdev.io.paymentsssm.domain.PaymentEvent;
import com.crpdev.io.paymentsssm.domain.PaymentState;
import com.crpdev.io.paymentsssm.services.PaymentServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;
import java.util.Random;

@Slf4j
@EnableStateMachineFactory
@Configuration
public class StateMachineConfig extends StateMachineConfigurerAdapter<PaymentState, PaymentEvent> {

    /**
     * Configure the states, setting the INITIAL state of the State Machine to NEW_PAYMENT
     * Define the possible terminal states as MAKE_PAYMENT & DENY_PAYMENT
     * @param states
     * @throws Exception
     */
    @Override
    public void configure(StateMachineStateConfigurer<PaymentState, PaymentEvent> states) throws Exception {
        states.withStates()
                .initial(PaymentState.NEW_PAYMENT)
                .states(EnumSet.allOf(PaymentState.class))
                .end(PaymentState.MAKE_PAYMENT)
                .end(PaymentState.DENY_PAYMENT);
    }

    /**
     * Configure the possible transitions possible in the state machine
     * @param transitions
     * @throws Exception
     */
    @Override
    public void configure(StateMachineTransitionConfigurer<PaymentState, PaymentEvent> transitions) throws Exception {
        transitions.withExternal().source(PaymentState.NEW_PAYMENT).target(PaymentState.NEW_PAYMENT).event(PaymentEvent.INIT_PAYMENT).action(verifyBalance())
                .and()
                .withExternal().source(PaymentState.NEW_PAYMENT).target(PaymentState.MAKE_PAYMENT).event(PaymentEvent.BAL_OK).action(printStatus())
                .and()
                .withExternal().source(PaymentState.NEW_PAYMENT).target(PaymentState.OVERDRAFT).event(PaymentEvent.BAL_KO).action(printStatus())
                .and()
                .withExternal().source(PaymentState.OVERDRAFT).target(PaymentState.OVERDRAFT).event(PaymentEvent.GET_OVERDRAFT_CD).action(getOverrideCode())
                .and()
                .withExternal().source(PaymentState.OVERDRAFT).target(PaymentState.MAKE_PAYMENT).event(PaymentEvent.OVERDRAFT_OK).action(printStatus())
                .and()
                .withExternal().source(PaymentState.OVERDRAFT).target(PaymentState.DENY_PAYMENT).event(PaymentEvent.OVERDRAFT_KO).action(printStatus());
    }


    /**
     * Listener to pick up any state change event that happens in the State Machine
     * @param config
     * @throws Exception
     */
    @Override
    public void configure(StateMachineConfigurationConfigurer<PaymentState, PaymentEvent> config) throws Exception {
        StateMachineListenerAdapter<PaymentState, PaymentEvent> adapter = new StateMachineListenerAdapter(){
            @Override
            public void stateChanged(State from, State to) {
                log.info(String.format("State Changed From : %s to %s", from, to));
            }
        };

        config.withConfiguration().listener(adapter);
    }

    /**
     * Actions are any possible custom activity that needs to be carried out during a transition/ event, before the TARGET state is set
     * @return
     */
    public Action<PaymentState, PaymentEvent> getOverrideCode(){

        return ctx -> {
          log.info("<<< Override Code service has been called >>>");
          if(new Random().nextInt(10) < 8){
               log.info("Override auth request Approved");
              log.info("<<< Setting event to BAL_OK which should move the state to MAKE_PAYMENT>>>");
               ctx.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.OVERDRAFT_OK).setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, ctx.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)).build());
          } else {
              log.info("Override auth request Rejected");
              log.info("<<< Setting event to BAL_KO which should move the state to DENY_PAYMENT>>>");
              ctx.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.OVERDRAFT_KO).setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, ctx.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)).build());
          }

        };
    }

    public Action<PaymentState, PaymentEvent> verifyBalance(){

        return ctx -> {
            log.info("<<< Verify Balance service has been called >>>");
            if(new Random().nextInt(10) > 8){
                log.info("<<< Balance available to perform payment >>>");
                log.info("<<< Setting event to BAL_OK which should move the state to MAKE_PAYMENT>>>");
                ctx.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.BAL_OK).setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, ctx.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)).build());
            } else {
                log.info("<<< Balance not available to perform payment >>>");
                log.info("<<< Setting event to BAL_KO which should move the state to OVERDRAFT>>>");
                ctx.getStateMachine().sendEvent(MessageBuilder.withPayload(PaymentEvent.BAL_KO).setHeader(PaymentServiceImpl.PAYMENT_ID_HEADER, ctx.getMessageHeader(PaymentServiceImpl.PAYMENT_ID_HEADER)).build());
            }

        };
    }

    public Action<PaymentState, PaymentEvent> printStatus(){

        return ctx -> {
            log.info("<<< printStatus has been called >>>");
            log.info(ctx.getStateMachine().getState().toString());
        };
    }
}
