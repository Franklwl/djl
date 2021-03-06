/*
 * Copyright 2020 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"). You may not use this file except in compliance
 * with the License. A copy of the License is located at
 *
 * http://aws.amazon.com/apache2.0/
 *
 * or in the "license" file accompanying this file. This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES
 * OR CONDITIONS OF ANY KIND, either express or implied. See the License for the specific language governing permissions
 * and limitations under the License.
 */
package ai.djl.paddlepaddle.zoo.cv.imageclassification;

import ai.djl.Model;
import ai.djl.modality.Classifications;
import ai.djl.modality.cv.Image;
import ai.djl.modality.cv.transform.Normalize;
import ai.djl.modality.cv.transform.Resize;
import ai.djl.modality.cv.transform.ToTensor;
import ai.djl.modality.cv.translator.ImageClassificationTranslator;
import ai.djl.modality.cv.zoo.ImageClassificationModelLoader;
import ai.djl.repository.Repository;
import ai.djl.repository.zoo.ModelZoo;
import ai.djl.translate.Translator;
import ai.djl.translate.TranslatorFactory;
import ai.djl.util.Pair;
import java.util.Map;

/** Model loader for Mask classification models. */
public class PpMaskClassification extends ImageClassificationModelLoader {

    /**
     * Creates the Model loader from the given repository.
     *
     * @param repository the repository to load the model from
     * @param groupId the group id of the model
     * @param artifactId the artifact id of the model
     * @param version the version number of the model
     * @param modelZoo the modelZoo type that is being used to get supported engine types
     */
    public PpMaskClassification(
            Repository repository,
            String groupId,
            String artifactId,
            String version,
            ModelZoo modelZoo) {
        super(repository, groupId, artifactId, version, modelZoo);
        // override TranslatorFactory
        factories.put(new Pair<>(Image.class, Classifications.class), new FactoryImpl());
    }

    private static final class FactoryImpl implements TranslatorFactory<Image, Classifications> {

        /** {@inheritDoc} */
        @Override
        public Translator<Image, Classifications> newInstance(
                Model model, Map<String, ?> arguments) {
            return ImageClassificationTranslator.builder()
                    .addTransform(new Resize(128, 128))
                    .addTransform(new ToTensor())
                    .addTransform(
                            new Normalize(
                                    new float[] {0.5f, 0.5f, 0.5f}, new float[] {1.0f, 1.0f, 1.0f}))
                    .addTransform(nd -> nd.flip(0)) // RGB -> GBR
                    .build();
        }
    }
}
