/*
 *  Copyright 1999-2018 Alibaba Group Holding Ltd.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.seata.discovery.registry.sofa;

import com.alipay.sofa.registry.server.test.TestRegistryMain;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
/**
 * The type SofaRegistryServiceImpl test.
 *
 * @author leizhiyuan
 */
public class SofaRegistryServiceImplTest {

    private static TestRegistryMain registryMain;

    @BeforeClass
    public static void beforeClass() {
        System.setProperty("serverAddr", "127.0.0.1:9603");
        System.setProperty("addressWaitTime", "10000");
        registryMain = new TestRegistryMain();
        try {
            registryMain.startRegistry();
        } catch (Exception e) {
            Assert.fail("start sofaregistry fail");
        }
    }

    @Test
    public void testSofaRegistry() {
        final InetSocketAddress address = new InetSocketAddress(1234);

        final SofaRegistryServiceImpl instance = SofaRegistryServiceImpl.getInstance();
        try {
            instance.register(address);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        //need sofa registry to sync data
        try {
            TimeUnit.SECONDS.sleep(10);
        } catch (InterruptedException e) {
        }

        List<InetSocketAddress> result = new ArrayList<>();
        try {
            result = instance.lookup("my_test_tx_group");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertTrue(result.size() > 0);
        Assert.assertEquals(address, result.get(0));


        try {
            instance.unregister(address);
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        try {
            TimeUnit.SECONDS.sleep(3);
        } catch (InterruptedException ignore) {
        }

        try {
            result = instance.lookup("my_test_tx_group");
        } catch (Exception e) {
            Assert.fail(e.getMessage());
        }

        Assert.assertEquals(0, result.size());

    }


    @AfterClass
    public static void afterClass() {
        System.setProperty("serverAddr", "");
        System.setProperty("addressWaitTime", "0");


        try {
            registryMain.stopRegistry();
        } catch (Exception ignore) {
            //ignore
        }
    }

}