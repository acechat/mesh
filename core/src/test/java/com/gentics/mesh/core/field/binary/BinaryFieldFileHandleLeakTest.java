package com.gentics.mesh.core.field.binary;

import com.gentics.mesh.core.data.node.Node;
import com.gentics.mesh.core.rest.node.NodeResponse;
import com.gentics.mesh.parameter.impl.ImageManipulationParametersImpl;
import com.gentics.mesh.test.context.AbstractMeshTest;
import com.gentics.mesh.test.context.MeshTestSetting;
import com.syncleus.ferma.tx.Tx;
import io.vertx.core.buffer.Buffer;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicReference;

import static com.gentics.mesh.test.ClientHelper.call;
import static com.gentics.mesh.test.TestDataProvider.PROJECT_NAME;
import static com.gentics.mesh.test.TestSize.FULL;

@MeshTestSetting(useElasticsearch = false, testSize = FULL, startServer = true)
public class BinaryFieldFileHandleLeakTest extends AbstractMeshTest {
    /**
     * Tests if the file handles are closed correctly after downloading binaries.
     */
    @Test
    public void testFileHandleLeakOnDownload() throws Exception {
        String contentType = "image/png";
        String fieldName = "image";
        String fileName = "somefile.png";
        Node node = folder("news");

        try (Tx tx = tx()) {
            prepareSchema(node, "", fieldName);
            tx.success();
        }
        try (Tx tx = tx()) {
            uploadImage(node, "en", fieldName, fileName, contentType);
            tx.success();
        }

        assertClosedFileHandleDifference(10, () -> {
            for (int i = 0; i < 100; i++) {
                client().downloadBinaryField(PROJECT_NAME, node.getUuid(), "en", fieldName).toSingle().blockingGet();
            }
        });
    }

    /**
     * Tests if the file handles are closed correctly after downloading binaries.
     */
    @Test
    public void testFileHandleLeakOnTransformation() throws Exception {
        String contentType = "image/png";
        String fieldName = "image";
        String fileName = "somefile.png";
        Node node = folder("news");

        try (Tx tx = tx()) {
            prepareSchema(node, "", fieldName);
            tx.success();
        }
        try (Tx tx = tx()) {
            uploadImage(node, "en", fieldName, fileName, contentType);
            tx.success();
        }
        NodeResponse initialResponse;
        try (Tx tx = tx()) {
            initialResponse = call(() -> client().findNodeByUuid(PROJECT_NAME, node.getUuid()));
            tx.success();
        }

        assertClosedFileHandleDifference(10, () -> {
            AtomicReference<NodeResponse> atomicResponse = new AtomicReference<>(initialResponse);
            NodeResponse response = atomicResponse.get();
            for (int i = 0; i < 100; i++) {
                client().transformNodeBinaryField(PROJECT_NAME, response.getUuid(), response.getLanguage(), response.getVersion(),
                    fieldName, new ImageManipulationParametersImpl().setWidth(100 + i))
                .toSingle().blockingGet();
            }
        });
    }

    /**
     * Tests if the file handles are closed correctly after downloading binaries.
     */
    @Test
    public void testFileHandleLeakOnImageManipulation() throws Exception {
        String contentType = "image/png";
        String fieldName = "image";
        String fileName = "somefile.png";
        Node node = folder("news");

        try (Tx tx = tx()) {
            prepareSchema(node, "", fieldName);
            tx.success();
        }
        try (Tx tx = tx()) {
            uploadImage(node, "en", fieldName, fileName, contentType);
            tx.success();
        }

        assertClosedFileHandleDifference(5, () -> {
            for (int i = 0; i < 10; i++) {
                client().downloadBinaryField(PROJECT_NAME, node.getUuid(), "en", fieldName, new ImageManipulationParametersImpl().setWidth(100 + i))
                    .toSingle().blockingGet();
            }
        });
    }

    /**
     * Tests if the file handles are closed correctly after downloading binaries.
     */
    @Test
    public void testFileHandleLeakOnUpload() throws Exception {
        String contentType = "text/plain";
        String fieldName = "image";
        String fileName = "somefile.txt";
        Node node = folder("news");

        try (Tx tx = tx()) {
            prepareSchema(node, "", fieldName);
            tx.success();
        }
        NodeResponse initialResponse;
        try (Tx tx = tx()) {
            initialResponse = call(() -> client().findNodeByUuid(PROJECT_NAME, node.getUuid()));
            tx.success();
        }

        assertClosedFileHandleDifference(10, () -> {
            AtomicReference<NodeResponse> atomicResponse = new AtomicReference<>(initialResponse);
            for (int i = 0; i < 100; i++) {
                Buffer buffer = Buffer.buffer("Testbuffer" + i);
                NodeResponse response = atomicResponse.get();
                atomicResponse.set(call(() -> client().updateNodeBinaryField(PROJECT_NAME, response.getUuid(), response.getLanguage(), response.getVersion(), fieldName, buffer,
                    fileName, contentType)));
            }
        });
    }

}