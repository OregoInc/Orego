package com.orego.corporation.orego.models.portrait.personModel;

import com.orego.corporation.orego.models.portrait.personModel.exceptions.NotLoadedBufferException;

import java.io.InputStream;
import java.nio.FloatBuffer;

/**
 * Класс PersonPart
 */

public abstract class PersonPart {


    private ModelLoader modelLoader;

    /**
     * @param inputStream InputStream .obj файла
     *                    <p>
     *                    В конструкторе происходит инициализация загрузчика 3D модели ModelLoader,
     *                    затем сразу же вызывается modelLoader.load(), что говорит нам о том,
     *                    что началась загрузка всех нужных нам файлов.
     */

    public PersonPart(final InputStream inputStream) {
        final ModelLoader modelLoader = new ModelLoader(inputStream);
        modelLoader.load();
        this.modelLoader = modelLoader;
    }


    /**
     * @return возвращает float буфер вершин;
     * @throws NotLoadedBufferException
     */

    public final FloatBuffer getVertexBufferObject() throws NotLoadedBufferException {
        final FloatBuffer buffer = modelLoader.getVertexBufferObject();
        if (buffer != null) {
            return buffer;
        } else {
            throw new NotLoadedBufferException("Not loaded" + getName() + "vertex buffer object");
        }
    }

    /**
     * @return возвращает int буфер вершин;
     * @throws NotLoadedBufferException
     */


    public final FloatBuffer getColorPerVertexObject() throws NotLoadedBufferException {
        final FloatBuffer buffer = modelLoader.getColorPerVertex();
        if (buffer != null) {
            return buffer;
        } else {
            throw new NotLoadedBufferException("Not loaded" + getName() + "element buffer object");
        }
    }

    /**
     * @return возвращает имя части головы
     */

    public abstract String getName();

    public ModelDimensions getDimension() {
        return modelLoader.getDimension();
    }

}
