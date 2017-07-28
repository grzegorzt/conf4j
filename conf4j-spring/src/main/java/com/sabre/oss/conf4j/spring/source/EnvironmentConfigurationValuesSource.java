/*
 * MIT License
 *
 * Copyright 2017 Sabre GLBL Inc.
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

package com.sabre.oss.conf4j.spring.source;

import com.sabre.oss.conf4j.source.ConfigurationValuesSource;
import com.sabre.oss.conf4j.source.OptionalValue;
import org.springframework.context.EnvironmentAware;
import org.springframework.core.env.Environment;

import static com.sabre.oss.conf4j.source.OptionalValue.absent;
import static com.sabre.oss.conf4j.source.OptionalValue.present;
import static java.util.Objects.requireNonNull;
import static org.springframework.util.Assert.state;

/**
 * {@link ConfigurationValuesSource} adapter to {@link Environment}.
 */
public class EnvironmentConfigurationValuesSource implements ConfigurationValuesSource, EnvironmentAware {
    private Environment environment;

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEnvironment(Environment environment) {
        this.environment = environment;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public OptionalValue<String> getValue(String key) {
        requireNonNull(key, "key cannot be null");
        state(environment != null, "environment is not set");

        String property = environment.getProperty(key);
        return property != null || environment.containsProperty(key) ? present(property) : absent();
    }
}