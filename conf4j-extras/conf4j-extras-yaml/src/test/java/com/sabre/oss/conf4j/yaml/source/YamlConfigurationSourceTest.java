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

package com.sabre.oss.conf4j.yaml.source;

import com.sabre.oss.conf4j.source.OptionalValue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.*;
import java.util.stream.Stream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.apache.commons.io.FileUtils.deleteQuietly;
import static org.apache.commons.io.FileUtils.write;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.util.Files.newTemporaryFile;
import static org.mockito.Mockito.*;

class YamlConfigurationSourceTest {
    private static final String yamlContent = "" +
            "collection:\n" +
            "- collectionValue\n" +
            "- nested:\n" +
            "       nestedCollectionValue\n" +
            "flat:\n" +
            "   flatValue\n" +
            "outer:\n" +
            "   inner:\n" +
            "       nestedValue\n";

    private YamlConfigurationSource configurationSource = new YamlConfigurationSource(new ByteArrayInputStream(yamlContent.getBytes()));

    @ParameterizedTest
    @MethodSource("existingProperties")
    void shouldReferToProperProperty(String propertyName, String expectedValue) {
        // when
        OptionalValue<String> propertyValue = configurationSource.getValue(propertyName, null);

        // then
        assertThat(propertyValue.isPresent()).isTrue();
        assertThat(propertyValue.get()).isEqualTo(expectedValue);
    }

    @Test
    void shouldBeAbsentWhenNoSuchProperty() {
        // given
        String propertyName = "not existing";

        // when
        OptionalValue<String> propertyValue = configurationSource.getValue(propertyName, null);

        // then
        assertThat(propertyValue.isAbsent()).isTrue();
        assertThatThrownBy(propertyValue::get)
                .isExactlyInstanceOf(IllegalStateException.class)
                .hasMessage("Value is absent.");
    }

    @Test
    void shouldConstructFromReader() throws IOException {
        // given
        Reader reader = spy(new StringReader(yamlContent));

        // when
        YamlConfigurationSource source = new YamlConfigurationSource(reader);

        // then
        assertThat(source.getAllConfigurationEntries()).isNotEmpty();
        verify(reader, times(1)).close();
    }

    @Test
    void shouldConstructFromInputStream() throws IOException {
        // given
        InputStream inputStream = spy(new ByteArrayInputStream(yamlContent.getBytes(UTF_8)));

        // when
        YamlConfigurationSource source = new YamlConfigurationSource(inputStream);

        // then
        assertThat(source.getAllConfigurationEntries()).isNotEmpty();
        verify(inputStream, times(1)).close();
    }

    @Test
    void shouldConstructFromFile() throws IOException {
        // given
        File file = newTemporaryFile();
        write(file, yamlContent, UTF_8);

        // when
        YamlConfigurationSource source = new YamlConfigurationSource(file);

        deleteQuietly(file);

        // then
        assertThat(source.getAllConfigurationEntries()).isNotEmpty();
    }

    private static Stream<Arguments> existingProperties() {
        return Stream.of(
                Arguments.of("flat", "flatValue"),
                Arguments.of("outer.inner", "nestedValue"),
                Arguments.of("collection[0]", "collectionValue"),
                Arguments.of("collection[1].nested", "nestedCollectionValue")
        );
    }
}
