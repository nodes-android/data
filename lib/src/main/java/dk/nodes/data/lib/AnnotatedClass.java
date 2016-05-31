package dk.nodes.data.lib;

/*
 * Copyright (C) 2015 Hannes Dorfmann
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import javax.lang.model.element.TypeElement;

import dk.nodes.data.annotation.Data;
import dk.nodes.data.annotation.Mode;
import dk.nodes.data.annotation.Persistence;

/**
 * Holds the information about a class annotated with @Data
 *
 * @author Hannes Dorfmann (Changes by joso@nodes.dk)
 */
public class AnnotatedClass {

    private TypeElement annotatedClassElement;
    private Persistence persistence;
    private Mode mode;

    public AnnotatedClass(TypeElement classElement) {
        this.annotatedClassElement = classElement;
        Data annotation = classElement.getAnnotation(Data.class);
        persistence = annotation.persistence();
        mode = annotation.mode();
    }

    /**
     *
     * @return Persistence mode (enum)
     */
    public Persistence getPersistence() {
        return persistence;
    }

    /**
     * @return Mode save method (enum)
     */
    public Mode getMode() {
        return mode;
    }

    /**
     * @return File name (string) in format data_%s_file
     */
    public String getKey() {
        return String.format("data_%s_file", annotatedClassElement.getSimpleName().toString());
    }

    /**
     * The original element that was annotated with @Factory
     */
    public TypeElement getTypeElement() {
        return annotatedClassElement;
    }
}
