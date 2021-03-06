/**
 * Copyright (c) 2015 Bosch Software Innovations GmbH and others.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 */
package org.eclipse.hawkbit.repository.event.remote.entity;

import org.eclipse.hawkbit.repository.event.remote.entity.RolloutUpdatedEvent;
import org.eclipse.hawkbit.repository.jpa.model.JpaRollout;
import org.eclipse.hawkbit.repository.model.DistributionSet;
import org.eclipse.hawkbit.repository.model.Rollout;
import org.eclipse.hawkbit.repository.model.RolloutGroup.RolloutGroupSuccessCondition;
import org.eclipse.hawkbit.repository.model.RolloutGroupConditionBuilder;
import org.junit.Test;

import ru.yandex.qatools.allure.annotations.Description;
import ru.yandex.qatools.allure.annotations.Features;
import ru.yandex.qatools.allure.annotations.Stories;

/**
 * Test the remote entity events.
 */
@Features("Component Tests - Repository")
@Stories("Test RolloutUpdatedEvent")
public class RolloutEventTest extends AbstractRemoteEntityEventTest<Rollout> {

    @Test
    @Description("Verifies that the rollout entity reloading by remote updated event works")
    public void testRolloutUpdatedEvent() {
        assertAndCreateRemoteEvent(RolloutUpdatedEvent.class);
    }

    @Override
    protected Rollout createEntity() {
        targetManagement.createTarget(entityFactory.generateTarget("12345"));
        final DistributionSet ds = distributionSetManagement
                .createDistributionSet(entityFactory.generateDistributionSet("incomplete", "2", "incomplete",
                        distributionSetManagement.findDistributionSetTypeByKey("os"), null));

        final Rollout rollout = entityFactory.generateRollout();
        rollout.setName("exampleRollout");
        rollout.setTargetFilterQuery("controllerId==*");
        rollout.setDistributionSet(ds);

        final JpaRollout entity = (JpaRollout) rolloutManagement.createRollout(rollout, 10,
                new RolloutGroupConditionBuilder().successCondition(RolloutGroupSuccessCondition.THRESHOLD, "10")
                        .build());

        return entity;
    }

}
