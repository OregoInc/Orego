package com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.portrait.personModel;


import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

final class ModelLoader {

    private boolean isLoaded;

    private final InputStream bytes;

    private FloatBuffer vertexBufferObject;
    private FloatBuffer colorPerVertexObject;

    private int size = 0;
    private ModelDimensions modelDimension;

    ModelLoader(final InputStream bytes) {
        this.isLoaded = false;
        this.bytes = bytes;
    }

    final void load() {
        if (!isLoaded) {
            byte[] vBytes;
            try {
                vBytes = IOUtils.toByteArray(bytes);
                size = vBytes.length;
                vertexBufferObject = ByteBuffer.allocateDirect(vBytes.length / 2).asFloatBuffer();
                colorPerVertexObject = ByteBuffer.allocateDirect(vBytes.length / 2).asFloatBuffer();

                for (int i = 0; i < vBytes.length; i += 24) {
                    vertexBufferObject.put(ByteBuffer.wrap(new byte[]{vBytes[i], vBytes[i + 1]
                            , vBytes[i + 2], vBytes[i + 3]}).getFloat());
                    vertexBufferObject.put(ByteBuffer.wrap(new byte[]{vBytes[i + 4], vBytes[i + 5]
                            , vBytes[i + 6], vBytes[i + 7]}).getFloat());
                    vertexBufferObject.put(ByteBuffer.wrap(new byte[]{vBytes[i + 8], vBytes[i + 9]
                            , vBytes[i + 10], vBytes[i + 11]}).getFloat());
                    colorPerVertexObject.put(ByteBuffer.wrap(new byte[]{vBytes[i + 12], vBytes[i + 13]
                            , vBytes[i + 14], vBytes[i + 15]}).getFloat());
                    colorPerVertexObject.put(ByteBuffer.wrap(new byte[]{vBytes[i + 16], vBytes[i + 17]
                            , vBytes[i + 18], vBytes[i + 19]}).getFloat());
                    colorPerVertexObject.put(ByteBuffer.wrap(new byte[]{vBytes[i + 20], vBytes[i + 21]
                            , vBytes[i + 22], vBytes[i + 23]}).getFloat());
                }
                modelDimension = new ModelDimensions();
                modelDimension.set(vertexBufferObject.get(0), vertexBufferObject.get(1)
                        , vertexBufferObject.get(2));
                for (int i = 0; i < size / 8; i += 3) {
                    modelDimension.update(vertexBufferObject.get(i), vertexBufferObject.get(i + 1), vertexBufferObject.get(i + 2));
                }
                vertexBufferObject.position(0);
                colorPerVertexObject.position(0);
            } catch (IOException e) {
                e.printStackTrace();
            }
            isLoaded = true;
        }
    }

    final FloatBuffer getVertexBufferObject() {
        return isLoaded ? vertexBufferObject : null;
    }

    final FloatBuffer getColorPerVertex() {
        return isLoaded ? colorPerVertexObject : null;
    }

    final int getVertexBufferSize() {
        return size / 2;
    }

    public final ModelDimensions getDimension() {
        return modelDimension;
    }
}