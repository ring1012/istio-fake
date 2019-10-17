/**
 * Copyright 2012-2019 The Feign Authors
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package istio.fake.base;

import istio.fake.openfake.Fake;
import istio.fake.base.codec.Decoder;
import istio.fake.base.codec.Encoder;
import istio.fake.base.codec.ErrorDecoder;
import istio.fake.base.log.FakeLogger;
import istio.fake.openfake.Target;

/**
 * Allows Feign interfaces to return HystrixCommand or rx.Observable or rx.Single objects. Also
 * directly.
 */
public final class HystrixFake {

    private Contract contract;

    public static Builder builder(Contract contract) {
        return new Builder(contract);
    }

    public static final class Builder extends Fake.Builder {

        private Contract contract;

        public Builder() {
            super();
            contract = new Contract.Default();
        }

        public Builder(Contract contract) {
            super();
            this.contract = contract;
            if (contract == null) {
                this.contract = new Contract.Default();
            }
        }


        /**
         * @see #target(Class, String, Object)
         */
        public <T> T target(Target<T> target, T fallback) {
            return build(fallback != null ? new FallbackFactory.Default<T>(fallback) : null)
                    .newInstance(target);
        }

        /**
         * @see #target(Class, String, FallbackFactory)
         */
        public <T> T target(Target<T> target, FallbackFactory<? extends T> fallbackFactory) {
            return build(fallbackFactory).newInstance(target);
        }

        /**
         * fallback} support.
         *
         * <p>
         * Fallbacks are known values, which you return when there's an error invoking an http method.
         * For example, you can return a cached result as opposed to raising an error to the caller. To
         * use this feature, pass a safe implementation of your target interface as the last parameter.
         * <p>
         * Here's an example:
         *
         * <pre>
         * {@code
         *
         * // When dealing with fallbacks, it is less tedious to keep interfaces small.
         * interface GitHub {
         *   &#64;RequestLine("GET /repos/{owner}/{repo}/contributors")
         *   List<String> contributors(@Param("owner") String owner, @Param("repo") String repo);
         * }
         *
         * // This instance will be invoked if there are errors of any kind.
         * GitHub fallback = (owner, repo) -> {
         *   if (owner.equals("Netflix") && repo.equals("feign")) {
         *     return Arrays.asList("stuarthendren"); // inspired this approach!
         *   } else {
         *     return Collections.emptyList();
         *   }
         * };
         *
         * GitHub github = HystrixFeign.builder()
         *                             ...
         *                             .target(GitHub.class, "https://api.github.com", fallback);
         * }
         * </pre>
         *
         * @see #target(Target, Object)
         */
        public <T> T target(Class<T> apiType, String url, T fallback) {
            return target(new Target.HardCodedTarget<T>(apiType, url), fallback);
        }

        /**
         * Same as {@link #target(Class, String, T)}, except you can inspect a source exception before
         * creating a fallback object.
         */
        public <T> T target(Class<T> apiType,
                            String url,
                            FallbackFactory<? extends T> fallbackFactory) {
            return target(new Target.HardCodedTarget<T>(apiType, url), fallbackFactory);
        }

        @Override
        public Fake.Builder invocationHandlerFactory(InvocationHandlerFactory invocationHandlerFactory) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Builder contract(Contract contract) {
            this.contract = contract;
            return this;
        }

        @Override
        public Fake build() {
            return build(null);
        }

        /**
         * Configures components needed for hystrix integration.
         */
        Fake build(final FallbackFactory<?> nullableFallbackFactory) {
            super.invocationHandlerFactory(new InvocationHandlerFactory.Default());
            super.contract(new Contract.Default());
            return super.build();
        }

        // Covariant overrides to support chaining to new fallback method.
        @Override
        public Builder logLevel(FakeLogger.Level logLevel) {
            return (Builder) super.logLevel(logLevel);
        }

        @Override
        public Builder client(Client client) {
            return (Builder) super.client(client);
        }


        @Override
        public Builder logger(FakeLogger logger) {
            return (Builder) super.logger(logger);
        }

        @Override
        public Builder encoder(Encoder encoder) {
            return (Builder) super.encoder(encoder);
        }

        @Override
        public Builder decoder(Decoder decoder) {
            return (Builder) super.decoder(decoder);
        }

        @Override
        public Builder mapAndDecode(ResponseMapper mapper, Decoder decoder) {
            return (Builder) super.mapAndDecode(mapper, decoder);
        }

        @Override
        public Builder decode404() {
            return (Builder) super.decode404();
        }

        @Override
        public Builder errorDecoder(ErrorDecoder errorDecoder) {
            return (Builder) super.errorDecoder(errorDecoder);
        }

        @Override
        public Builder options(Request.Options options) {
            return (Builder) super.options(options);
        }

        @Override
        public Builder requestInterceptor(RequestInterceptor requestInterceptor) {
            return (Builder) super.requestInterceptor(requestInterceptor);
        }

        @Override
        public Builder requestInterceptors(Iterable<RequestInterceptor> requestInterceptors) {
            return (Builder) super.requestInterceptors(requestInterceptors);
        }
    }
}
