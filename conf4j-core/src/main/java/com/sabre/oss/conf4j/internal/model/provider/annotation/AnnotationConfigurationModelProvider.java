/*
 * MIT License
 *
 * Copyright 2017-2018 Sabre GLBL Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.sabre.oss.conf4j.internal.model.provider.annotation;

import com.sabre.oss.conf4j.annotation.FallbackKey;
import com.sabre.oss.conf4j.annotation.Key;
import com.sabre.oss.conf4j.internal.model.ConfigurationModelProvider;
import com.sabre.oss.conf4j.internal.model.provider.AbstractConfigurationModelProvider;

import static java.util.Arrays.asList;

/**
 * This class extract configuration model from conf4j annotations like {@link Key}, {@link FallbackKey}.
 */
public class AnnotationConfigurationModelProvider extends AbstractConfigurationModelProvider {
    private static final ConfigurationModelProvider instance = new AnnotationConfigurationModelProvider();

    /**
     * Provides {@link ConfigurationModelProvider} instance.
     *
     * @return configuration model provider instance.
     */
    public static ConfigurationModelProvider getInstance() {
        return instance;
    }

    protected AnnotationConfigurationModelProvider() {
        super(new AnnotationMetadataExtractor());
        this.methodParsers = asList(
                new AnnotationNonAbstractMethodParser(),
                new AnnotationValuePropertyMethodParser(metadataExtractor),
                new AnnotationSubConfigurationPropertyMethodParser(metadataExtractor, this),
                new AnnotationSubConfigurationListPropertyMethodParser(metadataExtractor, this),
                new UnannotatedAbstractPropertyMethodParser()
        );
    }
}
