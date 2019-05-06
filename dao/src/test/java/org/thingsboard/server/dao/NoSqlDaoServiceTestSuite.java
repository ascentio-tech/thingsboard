/**
 * Copyright © 2016-2019 The Thingsboard Authors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.thingsboard.server.dao;

import org.cassandraunit.dataset.cql.ClassPathCQLDataSet;
import org.junit.ClassRule;
import org.junit.extensions.cpsuite.ClasspathSuite;
import org.junit.extensions.cpsuite.ClasspathSuite.ClassnameFilters;
import org.junit.runner.RunWith;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.cassandraunit.utils.EmbeddedCassandraServerHelper.*;
import static org.junit.Assert.fail;

@RunWith(ClasspathSuite.class)
@ClassnameFilters({
        "org.thingsboard.server.dao.service.*ServiceNoSqlTest"
})
public class NoSqlDaoServiceTestSuite {

    /**
     * This is required on non Oracle JDK's due to this issue:
     *      https://github.com/jsevellec/cassandra-unit/issues/224
     */
    static {
        try {
            checkIfDirectBufferCleanerIsAccesible();
        }
        catch (Throwable t) {
            initializeEmbeddedCassandra();
        }
    }

    /**
     *
     * This is inspired in {@link org.apache.cassandra.io.util.FileUtils} static
     * initialization block.
     *
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    private static void checkIfDirectBufferCleanerIsAccesible() throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        ByteBuffer buf = ByteBuffer.allocateDirect(1);
        Class directBufferClass = Class.forName("sun.nio.ch.DirectBuffer");
        directBufferClass.cast(buf);
        final Method cleaner = directBufferClass.getMethod("cleaner");
        Object cleanerInstance =  cleaner.invoke(buf);
        final Method clean = Class.forName("jdk.internal.ref.Cleaner").getMethod("clean");
        clean.invoke(cleanerInstance);
    }

    /**
     * Initialize EmbeddedCassandra without invoking FileUtils to avoid non-Oracle JDK issue
     *
     */
    private static void initializeEmbeddedCassandra() {
        try {
            final URI uri = ClassLoader.getSystemResource("cassandra-test.yaml").toURI();
            final Path path = Paths.get(uri);
            final URI log4jProperties = ClassLoader.getSystemResource("log4j-embedded-cassandra.properties").toURI();
            System.setProperty("log4j.configuration", log4jProperties.getPath());
            startEmbeddedCassandra(path.toFile(),
                    DEFAULT_TMP_DIR,
                    DEFAULT_STARTUP_TIMEOUT);

        } catch (Exception e) {
            fail(String.format("Could not initialize EmbeddedCassandra: %s", e.getMessage()));
        }

    }

    @ClassRule
    public static CustomCassandraCQLUnit cassandraUnit =
            new CustomCassandraCQLUnit(
                    Arrays.asList(
                            new ClassPathCQLDataSet("cassandra/schema-ts.cql", false, false),
                            new ClassPathCQLDataSet("cassandra/schema-entities.cql", false, false),
                            new ClassPathCQLDataSet("cassandra/system-data.cql", false, false),
                            new ClassPathCQLDataSet("cassandra/system-test.cql", false, false)),
                    "cassandra-test.yaml", 30000L);

}
