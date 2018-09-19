package com.orego.corporation.orego.models.portrait.personModel.face;

import com.orego.corporation.orego.models.portrait.personModel.PersonPart;

import java.io.InputStream;

public final class Face extends PersonPart {

    public Face(final InputStream inputStream) {
        super(inputStream);
    }

    @Override
    public final String getName() {
        return "Face";
    }
}
