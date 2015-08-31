// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: query_task_status.proto

package com.mogujie.jarvis.protocol;

public final class QueryTaskStatusProtos {
    private QueryTaskStatusProtos() {
    }

    public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    }

    public interface RestServerQueryTaskStatusRequestOrBuilder extends
            // @@protoc_insertion_point(interface_extends:RestServerQueryTaskStatusRequest)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>required int64 task_id = 1;</code>
         */
        boolean hasTaskId();

        /**
         * <code>required int64 task_id = 1;</code>
         */
        long getTaskId();
    }

    /**
     * Protobuf type {@code RestServerQueryTaskStatusRequest}
     */
    public static final class RestServerQueryTaskStatusRequest extends com.google.protobuf.GeneratedMessage implements
            // @@protoc_insertion_point(message_implements:RestServerQueryTaskStatusRequest)
            RestServerQueryTaskStatusRequestOrBuilder {
        // Use RestServerQueryTaskStatusRequest.newBuilder() to construct.
        private RestServerQueryTaskStatusRequest(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.unknownFields = builder.getUnknownFields();
        }

        private RestServerQueryTaskStatusRequest(boolean noInit) {
            this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }

        private static final RestServerQueryTaskStatusRequest defaultInstance;

        public static RestServerQueryTaskStatusRequest getDefaultInstance() {
            return defaultInstance;
        }

        public RestServerQueryTaskStatusRequest getDefaultInstanceForType() {
            return defaultInstance;
        }

        private final com.google.protobuf.UnknownFieldSet unknownFields;

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private RestServerQueryTaskStatusRequest(com.google.protobuf.CodedInputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
            initFields();
            com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {
                            bitField0_ |= 0x00000001;
                            taskId_ = input.readInt64();
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(e.getMessage()).setUnfinishedMessage(this);
            } finally {
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
            return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_RestServerQueryTaskStatusRequest_descriptor;
        }

        protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_RestServerQueryTaskStatusRequest_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest.class,
                            com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest.Builder.class);
        }

        public static com.google.protobuf.Parser<RestServerQueryTaskStatusRequest> PARSER = new com.google.protobuf.AbstractParser<RestServerQueryTaskStatusRequest>() {
            public RestServerQueryTaskStatusRequest parsePartialFrom(com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
                return new RestServerQueryTaskStatusRequest(input, extensionRegistry);
            }
        };

        @java.lang.Override
        public com.google.protobuf.Parser<RestServerQueryTaskStatusRequest> getParserForType() {
            return PARSER;
        }

        private int bitField0_;
        public static final int TASK_ID_FIELD_NUMBER = 1;
        private long taskId_;

        /**
         * <code>required int64 task_id = 1;</code>
         */
        public boolean hasTaskId() {
            return ((bitField0_ & 0x00000001) == 0x00000001);
        }

        /**
         * <code>required int64 task_id = 1;</code>
         */
        public long getTaskId() {
            return taskId_;
        }

        private void initFields() {
            taskId_ = 0L;
        }

        private byte memoizedIsInitialized = -1;

        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1)
                return true;
            if (isInitialized == 0)
                return false;

            if (!hasTaskId()) {
                memoizedIsInitialized = 0;
                return false;
            }
            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
            getSerializedSize();
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                output.writeInt64(1, taskId_);
            }
            getUnknownFields().writeTo(output);
        }

        private int memoizedSerializedSize = -1;

        public int getSerializedSize() {
            int size = memoizedSerializedSize;
            if (size != -1)
                return size;

            size = 0;
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                size += com.google.protobuf.CodedOutputStream.computeInt64Size(1, taskId_);
            }
            size += getUnknownFields().getSerializedSize();
            memoizedSerializedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;

        @java.lang.Override
        protected java.lang.Object writeReplace() throws java.io.ObjectStreamException {
            return super.writeReplace();
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseFrom(
                com.google.protobuf.ByteString data) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseFrom(
                com.google.protobuf.ByteString data, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                        throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseFrom(byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return PARSER.parseFrom(input);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseFrom(java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseDelimitedFrom(java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseFrom(
                com.google.protobuf.CodedInputStream input) throws java.io.IOException {
            return PARSER.parseFrom(input);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parseFrom(
                com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(com.google.protobuf.GeneratedMessage.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code RestServerQueryTaskStatusRequest}
         */
        public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder>implements
                // @@protoc_insertion_point(builder_implements:RestServerQueryTaskStatusRequest)
                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequestOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
                return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_RestServerQueryTaskStatusRequest_descriptor;
            }

            protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_RestServerQueryTaskStatusRequest_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest.class,
                                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest.Builder.class);
            }

            // Construct using
            // com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(com.google.protobuf.GeneratedMessage.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
                }
            }

            private static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                taskId_ = 0L;
                bitField0_ = (bitField0_ & ~0x00000001);
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
                return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_RestServerQueryTaskStatusRequest_descriptor;
            }

            public com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest getDefaultInstanceForType() {
                return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest.getDefaultInstance();
            }

            public com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest build() {
                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest buildPartial() {
                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest result = new com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest(
                        this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
                    to_bitField0_ |= 0x00000001;
                }
                result.taskId_ = taskId_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest) {
                    return mergeFrom((com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest other) {
                if (other == com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest.getDefaultInstance())
                    return this;
                if (other.hasTaskId()) {
                    setTaskId(other.getTaskId());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                if (!hasTaskId()) {

                    return false;
                }
                return true;
            }

            public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (com.mogujie.jarvis.protocol.QueryTaskStatusProtos.RestServerQueryTaskStatusRequest) e.getUnfinishedMessage();
                    throw e;
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int bitField0_;

            private long taskId_;

            /**
             * <code>required int64 task_id = 1;</code>
             */
            public boolean hasTaskId() {
                return ((bitField0_ & 0x00000001) == 0x00000001);
            }

            /**
             * <code>required int64 task_id = 1;</code>
             */
            public long getTaskId() {
                return taskId_;
            }

            /**
             * <code>required int64 task_id = 1;</code>
             */
            public Builder setTaskId(long value) {
                bitField0_ |= 0x00000001;
                taskId_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>required int64 task_id = 1;</code>
             */
            public Builder clearTaskId() {
                bitField0_ = (bitField0_ & ~0x00000001);
                taskId_ = 0L;
                onChanged();
                return this;
            }

            // @@protoc_insertion_point(builder_scope:RestServerQueryTaskStatusRequest)
        }

        static {
            defaultInstance = new RestServerQueryTaskStatusRequest(true);
            defaultInstance.initFields();
        }

        // @@protoc_insertion_point(class_scope:RestServerQueryTaskStatusRequest)
    }

    public interface ServerQueryTaskStatusResponseOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ServerQueryTaskStatusResponse)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>required int32 status = 1;</code>
         */
        boolean hasStatus();

        /**
         * <code>required int32 status = 1;</code>
         */
        int getStatus();
    }

    /**
     * Protobuf type {@code ServerQueryTaskStatusResponse}
     */
    public static final class ServerQueryTaskStatusResponse extends com.google.protobuf.GeneratedMessage implements
            // @@protoc_insertion_point(message_implements:ServerQueryTaskStatusResponse)
            ServerQueryTaskStatusResponseOrBuilder {
        // Use ServerQueryTaskStatusResponse.newBuilder() to construct.
        private ServerQueryTaskStatusResponse(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.unknownFields = builder.getUnknownFields();
        }

        private ServerQueryTaskStatusResponse(boolean noInit) {
            this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }

        private static final ServerQueryTaskStatusResponse defaultInstance;

        public static ServerQueryTaskStatusResponse getDefaultInstance() {
            return defaultInstance;
        }

        public ServerQueryTaskStatusResponse getDefaultInstanceForType() {
            return defaultInstance;
        }

        private final com.google.protobuf.UnknownFieldSet unknownFields;

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private ServerQueryTaskStatusResponse(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                throws com.google.protobuf.InvalidProtocolBufferException {
            initFields();
            com.google.protobuf.UnknownFieldSet.Builder unknownFields = com.google.protobuf.UnknownFieldSet.newBuilder();
            try {
                boolean done = false;
                while (!done) {
                    int tag = input.readTag();
                    switch (tag) {
                        case 0:
                            done = true;
                            break;
                        default: {
                            if (!parseUnknownField(input, unknownFields, extensionRegistry, tag)) {
                                done = true;
                            }
                            break;
                        }
                        case 8: {
                            bitField0_ |= 0x00000001;
                            status_ = input.readInt32();
                            break;
                        }
                    }
                }
            } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                throw e.setUnfinishedMessage(this);
            } catch (java.io.IOException e) {
                throw new com.google.protobuf.InvalidProtocolBufferException(e.getMessage()).setUnfinishedMessage(this);
            } finally {
                this.unknownFields = unknownFields.build();
                makeExtensionsImmutable();
            }
        }

        public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
            return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_ServerQueryTaskStatusResponse_descriptor;
        }

        protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_ServerQueryTaskStatusResponse_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse.class,
                            com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse.Builder.class);
        }

        public static com.google.protobuf.Parser<ServerQueryTaskStatusResponse> PARSER = new com.google.protobuf.AbstractParser<ServerQueryTaskStatusResponse>() {
            public ServerQueryTaskStatusResponse parsePartialFrom(com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
                return new ServerQueryTaskStatusResponse(input, extensionRegistry);
            }
        };

        @java.lang.Override
        public com.google.protobuf.Parser<ServerQueryTaskStatusResponse> getParserForType() {
            return PARSER;
        }

        private int bitField0_;
        public static final int STATUS_FIELD_NUMBER = 1;
        private int status_;

        /**
         * <code>required int32 status = 1;</code>
         */
        public boolean hasStatus() {
            return ((bitField0_ & 0x00000001) == 0x00000001);
        }

        /**
         * <code>required int32 status = 1;</code>
         */
        public int getStatus() {
            return status_;
        }

        private void initFields() {
            status_ = 0;
        }

        private byte memoizedIsInitialized = -1;

        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1)
                return true;
            if (isInitialized == 0)
                return false;

            if (!hasStatus()) {
                memoizedIsInitialized = 0;
                return false;
            }
            memoizedIsInitialized = 1;
            return true;
        }

        public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
            getSerializedSize();
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                output.writeInt32(1, status_);
            }
            getUnknownFields().writeTo(output);
        }

        private int memoizedSerializedSize = -1;

        public int getSerializedSize() {
            int size = memoizedSerializedSize;
            if (size != -1)
                return size;

            size = 0;
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                size += com.google.protobuf.CodedOutputStream.computeInt32Size(1, status_);
            }
            size += getUnknownFields().getSerializedSize();
            memoizedSerializedSize = size;
            return size;
        }

        private static final long serialVersionUID = 0L;

        @java.lang.Override
        protected java.lang.Object writeReplace() throws java.io.ObjectStreamException {
            return super.writeReplace();
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseFrom(com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseFrom(com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseFrom(byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return PARSER.parseFrom(input);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseFrom(java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseDelimitedFrom(java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseFrom(
                com.google.protobuf.CodedInputStream input) throws java.io.IOException {
            return PARSER.parseFrom(input);
        }

        public static com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parseFrom(
                com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        public Builder toBuilder() {
            return newBuilder(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(com.google.protobuf.GeneratedMessage.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code ServerQueryTaskStatusResponse}
         */
        public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder>implements
                // @@protoc_insertion_point(builder_implements:ServerQueryTaskStatusResponse)
                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponseOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
                return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_ServerQueryTaskStatusResponse_descriptor;
            }

            protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_ServerQueryTaskStatusResponse_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse.class,
                                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse.Builder.class);
            }

            // Construct using
            // com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse.newBuilder()
            private Builder() {
                maybeForceBuilderInitialization();
            }

            private Builder(com.google.protobuf.GeneratedMessage.BuilderParent parent) {
                super(parent);
                maybeForceBuilderInitialization();
            }

            private void maybeForceBuilderInitialization() {
                if (com.google.protobuf.GeneratedMessage.alwaysUseFieldBuilders) {
                }
            }

            private static Builder create() {
                return new Builder();
            }

            public Builder clear() {
                super.clear();
                status_ = 0;
                bitField0_ = (bitField0_ & ~0x00000001);
                return this;
            }

            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
                return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.internal_static_ServerQueryTaskStatusResponse_descriptor;
            }

            public com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse getDefaultInstanceForType() {
                return com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse.getDefaultInstance();
            }

            public com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse build() {
                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            public com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse buildPartial() {
                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse result = new com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse(
                        this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
                    to_bitField0_ |= 0x00000001;
                }
                result.status_ = status_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse) {
                    return mergeFrom((com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse other) {
                if (other == com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse.getDefaultInstance())
                    return this;
                if (other.hasStatus()) {
                    setStatus(other.getStatus());
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            public final boolean isInitialized() {
                if (!hasStatus()) {

                    return false;
                }
                return true;
            }

            public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (com.mogujie.jarvis.protocol.QueryTaskStatusProtos.ServerQueryTaskStatusResponse) e.getUnfinishedMessage();
                    throw e;
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int bitField0_;

            private int status_;

            /**
             * <code>required int32 status = 1;</code>
             */
            public boolean hasStatus() {
                return ((bitField0_ & 0x00000001) == 0x00000001);
            }

            /**
             * <code>required int32 status = 1;</code>
             */
            public int getStatus() {
                return status_;
            }

            /**
             * <code>required int32 status = 1;</code>
             */
            public Builder setStatus(int value) {
                bitField0_ |= 0x00000001;
                status_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>required int32 status = 1;</code>
             */
            public Builder clearStatus() {
                bitField0_ = (bitField0_ & ~0x00000001);
                status_ = 0;
                onChanged();
                return this;
            }

            // @@protoc_insertion_point(builder_scope:ServerQueryTaskStatusResponse)
        }

        static {
            defaultInstance = new ServerQueryTaskStatusResponse(true);
            defaultInstance.initFields();
        }

        // @@protoc_insertion_point(class_scope:ServerQueryTaskStatusResponse)
    }

    private static final com.google.protobuf.Descriptors.Descriptor internal_static_RestServerQueryTaskStatusRequest_descriptor;
    private static com.google.protobuf.GeneratedMessage.FieldAccessorTable internal_static_RestServerQueryTaskStatusRequest_fieldAccessorTable;
    private static final com.google.protobuf.Descriptors.Descriptor internal_static_ServerQueryTaskStatusResponse_descriptor;
    private static com.google.protobuf.GeneratedMessage.FieldAccessorTable internal_static_ServerQueryTaskStatusResponse_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

    static {
        java.lang.String[] descriptorData = { "\n\027query_task_status.proto\"3\n RestServerQ"
                + "ueryTaskStatusRequest\022\017\n\007task_id\030\001 \002(\003\"/" + "\n\035ServerQueryTaskStatusResponse\022\016\n\006statu"
                + "s\030\001 \002(\005B4\n\033com.mogujie.jarvis.protocolB\025" + "QueryTaskStatusProtos" };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            public com.google.protobuf.ExtensionRegistry assignDescriptors(com.google.protobuf.Descriptors.FileDescriptor root) {
                descriptor = root;
                return null;
            }
        };
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
                new com.google.protobuf.Descriptors.FileDescriptor[] {}, assigner);
        internal_static_RestServerQueryTaskStatusRequest_descriptor = getDescriptor().getMessageTypes().get(0);
        internal_static_RestServerQueryTaskStatusRequest_fieldAccessorTable = new com.google.protobuf.GeneratedMessage.FieldAccessorTable(
                internal_static_RestServerQueryTaskStatusRequest_descriptor, new java.lang.String[] { "TaskId", });
        internal_static_ServerQueryTaskStatusResponse_descriptor = getDescriptor().getMessageTypes().get(1);
        internal_static_ServerQueryTaskStatusResponse_fieldAccessorTable = new com.google.protobuf.GeneratedMessage.FieldAccessorTable(
                internal_static_ServerQueryTaskStatusResponse_descriptor, new java.lang.String[] { "Status", });
    }

    // @@protoc_insertion_point(outer_class_scope)
}
