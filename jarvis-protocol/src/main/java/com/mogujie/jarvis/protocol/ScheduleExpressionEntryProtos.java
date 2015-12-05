// Generated by the protocol buffer compiler. DO NOT EDIT!
// source: schedule_expression_entry.proto

package com.mogujie.jarvis.protocol;

public final class ScheduleExpressionEntryProtos {
    private ScheduleExpressionEntryProtos() {
    }

    public static void registerAllExtensions(com.google.protobuf.ExtensionRegistry registry) {
    }

    public interface ScheduleExpressionEntryOrBuilder extends
            // @@protoc_insertion_point(interface_extends:ScheduleExpressionEntry)
            com.google.protobuf.MessageOrBuilder {

        /**
         * <code>required int32 expression_type = 1;</code>
         */
        boolean hasExpressionType();

        /**
         * <code>required int32 expression_type = 1;</code>
         */
        int getExpressionType();

        /**
         * <code>required string schedule_expression = 2;</code>
         */
        boolean hasScheduleExpression();

        /**
         * <code>required string schedule_expression = 2;</code>
         */
        java.lang.String getScheduleExpression();

        /**
         * <code>required string schedule_expression = 2;</code>
         */
        com.google.protobuf.ByteString getScheduleExpressionBytes();
    }

    /**
     * Protobuf type {@code ScheduleExpressionEntry}
     */
    public static final class ScheduleExpressionEntry extends com.google.protobuf.GeneratedMessage implements
            // @@protoc_insertion_point(message_implements:ScheduleExpressionEntry)
            ScheduleExpressionEntryOrBuilder {
        // Use ScheduleExpressionEntry.newBuilder() to construct.
        private ScheduleExpressionEntry(com.google.protobuf.GeneratedMessage.Builder<?> builder) {
            super(builder);
            this.unknownFields = builder.getUnknownFields();
        }

        private ScheduleExpressionEntry(boolean noInit) {
            this.unknownFields = com.google.protobuf.UnknownFieldSet.getDefaultInstance();
        }

        private static final ScheduleExpressionEntry defaultInstance;

        public static ScheduleExpressionEntry getDefaultInstance() {
            return defaultInstance;
        }

        @Override
        public ScheduleExpressionEntry getDefaultInstanceForType() {
            return defaultInstance;
        }

        private final com.google.protobuf.UnknownFieldSet unknownFields;

        @java.lang.Override
        public final com.google.protobuf.UnknownFieldSet getUnknownFields() {
            return this.unknownFields;
        }

        private ScheduleExpressionEntry(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
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
                            expressionType_ = input.readInt32();
                            break;
                        }
                        case 18: {
                            com.google.protobuf.ByteString bs = input.readBytes();
                            bitField0_ |= 0x00000002;
                            scheduleExpression_ = bs;
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
            return com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.internal_static_ScheduleExpressionEntry_descriptor;
        }

        @Override
        protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
            return com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.internal_static_ScheduleExpressionEntry_fieldAccessorTable
                    .ensureFieldAccessorsInitialized(com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry.class,
                            com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry.Builder.class);
        }

        public static com.google.protobuf.Parser<ScheduleExpressionEntry> PARSER = new com.google.protobuf.AbstractParser<ScheduleExpressionEntry>() {
            @Override
            public ScheduleExpressionEntry parsePartialFrom(com.google.protobuf.CodedInputStream input,
                    com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
                return new ScheduleExpressionEntry(input, extensionRegistry);
            }
        };

        @java.lang.Override
        public com.google.protobuf.Parser<ScheduleExpressionEntry> getParserForType() {
            return PARSER;
        }

        private int bitField0_;
        public static final int EXPRESSION_TYPE_FIELD_NUMBER = 1;
        private int expressionType_;

        /**
         * <code>required int32 expression_type = 1;</code>
         */
        @Override
        public boolean hasExpressionType() {
            return ((bitField0_ & 0x00000001) == 0x00000001);
        }

        /**
         * <code>required int32 expression_type = 1;</code>
         */
        @Override
        public int getExpressionType() {
            return expressionType_;
        }

        public static final int SCHEDULE_EXPRESSION_FIELD_NUMBER = 2;
        private java.lang.Object scheduleExpression_;

        /**
         * <code>required string schedule_expression = 2;</code>
         */
        @Override
        public boolean hasScheduleExpression() {
            return ((bitField0_ & 0x00000002) == 0x00000002);
        }

        /**
         * <code>required string schedule_expression = 2;</code>
         */
        @Override
        public java.lang.String getScheduleExpression() {
            java.lang.Object ref = scheduleExpression_;
            if (ref instanceof java.lang.String) {
                return (java.lang.String) ref;
            } else {
                com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
                java.lang.String s = bs.toStringUtf8();
                if (bs.isValidUtf8()) {
                    scheduleExpression_ = s;
                }
                return s;
            }
        }

        /**
         * <code>required string schedule_expression = 2;</code>
         */
        @Override
        public com.google.protobuf.ByteString getScheduleExpressionBytes() {
            java.lang.Object ref = scheduleExpression_;
            if (ref instanceof java.lang.String) {
                com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
                scheduleExpression_ = b;
                return b;
            } else {
                return (com.google.protobuf.ByteString) ref;
            }
        }

        private void initFields() {
            expressionType_ = 0;
            scheduleExpression_ = "";
        }

        private byte memoizedIsInitialized = -1;

        @Override
        public final boolean isInitialized() {
            byte isInitialized = memoizedIsInitialized;
            if (isInitialized == 1)
                return true;
            if (isInitialized == 0)
                return false;

            if (!hasExpressionType()) {
                memoizedIsInitialized = 0;
                return false;
            }
            if (!hasScheduleExpression()) {
                memoizedIsInitialized = 0;
                return false;
            }
            memoizedIsInitialized = 1;
            return true;
        }

        @Override
        public void writeTo(com.google.protobuf.CodedOutputStream output) throws java.io.IOException {
            getSerializedSize();
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                output.writeInt32(1, expressionType_);
            }
            if (((bitField0_ & 0x00000002) == 0x00000002)) {
                output.writeBytes(2, getScheduleExpressionBytes());
            }
            getUnknownFields().writeTo(output);
        }

        private int memoizedSerializedSize = -1;

        @Override
        public int getSerializedSize() {
            int size = memoizedSerializedSize;
            if (size != -1)
                return size;

            size = 0;
            if (((bitField0_ & 0x00000001) == 0x00000001)) {
                size += com.google.protobuf.CodedOutputStream.computeInt32Size(1, expressionType_);
            }
            if (((bitField0_ & 0x00000002) == 0x00000002)) {
                size += com.google.protobuf.CodedOutputStream.computeBytesSize(2, getScheduleExpressionBytes());
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

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseFrom(com.google.protobuf.ByteString data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseFrom(com.google.protobuf.ByteString data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseFrom(byte[] data)
                throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data);
        }

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseFrom(byte[] data,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws com.google.protobuf.InvalidProtocolBufferException {
            return PARSER.parseFrom(data, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseFrom(java.io.InputStream input)
                throws java.io.IOException {
            return PARSER.parseFrom(input);
        }

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseFrom(java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseDelimitedFrom(java.io.InputStream input)
                throws java.io.IOException {
            return PARSER.parseDelimitedFrom(input);
        }

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseDelimitedFrom(java.io.InputStream input,
                com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return PARSER.parseDelimitedFrom(input, extensionRegistry);
        }

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseFrom(
                com.google.protobuf.CodedInputStream input) throws java.io.IOException {
            return PARSER.parseFrom(input);
        }

        public static com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parseFrom(
                com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry) throws java.io.IOException {
            return PARSER.parseFrom(input, extensionRegistry);
        }

        public static Builder newBuilder() {
            return Builder.create();
        }

        @Override
        public Builder newBuilderForType() {
            return newBuilder();
        }

        public static Builder newBuilder(com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry prototype) {
            return newBuilder().mergeFrom(prototype);
        }

        @Override
        public Builder toBuilder() {
            return newBuilder(this);
        }

        @java.lang.Override
        protected Builder newBuilderForType(com.google.protobuf.GeneratedMessage.BuilderParent parent) {
            Builder builder = new Builder(parent);
            return builder;
        }

        /**
         * Protobuf type {@code ScheduleExpressionEntry}
         */
        public static final class Builder extends com.google.protobuf.GeneratedMessage.Builder<Builder> implements
                // @@protoc_insertion_point(builder_implements:ScheduleExpressionEntry)
                com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntryOrBuilder {
            public static final com.google.protobuf.Descriptors.Descriptor getDescriptor() {
                return com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.internal_static_ScheduleExpressionEntry_descriptor;
            }

            @Override
            protected com.google.protobuf.GeneratedMessage.FieldAccessorTable internalGetFieldAccessorTable() {
                return com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.internal_static_ScheduleExpressionEntry_fieldAccessorTable
                        .ensureFieldAccessorsInitialized(com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry.class,
                                com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry.Builder.class);
            }

            // Construct using com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry.newBuilder()
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

            @Override
            public Builder clear() {
                super.clear();
                expressionType_ = 0;
                bitField0_ = (bitField0_ & ~0x00000001);
                scheduleExpression_ = "";
                bitField0_ = (bitField0_ & ~0x00000002);
                return this;
            }

            @Override
            public Builder clone() {
                return create().mergeFrom(buildPartial());
            }

            @Override
            public com.google.protobuf.Descriptors.Descriptor getDescriptorForType() {
                return com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.internal_static_ScheduleExpressionEntry_descriptor;
            }

            @Override
            public com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry getDefaultInstanceForType() {
                return com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry.getDefaultInstance();
            }

            @Override
            public com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry build() {
                com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry result = buildPartial();
                if (!result.isInitialized()) {
                    throw newUninitializedMessageException(result);
                }
                return result;
            }

            @Override
            public com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry buildPartial() {
                com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry result = new com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry(
                        this);
                int from_bitField0_ = bitField0_;
                int to_bitField0_ = 0;
                if (((from_bitField0_ & 0x00000001) == 0x00000001)) {
                    to_bitField0_ |= 0x00000001;
                }
                result.expressionType_ = expressionType_;
                if (((from_bitField0_ & 0x00000002) == 0x00000002)) {
                    to_bitField0_ |= 0x00000002;
                }
                result.scheduleExpression_ = scheduleExpression_;
                result.bitField0_ = to_bitField0_;
                onBuilt();
                return result;
            }

            @Override
            public Builder mergeFrom(com.google.protobuf.Message other) {
                if (other instanceof com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry) {
                    return mergeFrom((com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry) other);
                } else {
                    super.mergeFrom(other);
                    return this;
                }
            }

            public Builder mergeFrom(com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry other) {
                if (other == com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry.getDefaultInstance())
                    return this;
                if (other.hasExpressionType()) {
                    setExpressionType(other.getExpressionType());
                }
                if (other.hasScheduleExpression()) {
                    bitField0_ |= 0x00000002;
                    scheduleExpression_ = other.scheduleExpression_;
                    onChanged();
                }
                this.mergeUnknownFields(other.getUnknownFields());
                return this;
            }

            @Override
            public final boolean isInitialized() {
                if (!hasExpressionType()) {

                    return false;
                }
                if (!hasScheduleExpression()) {

                    return false;
                }
                return true;
            }

            @Override
            public Builder mergeFrom(com.google.protobuf.CodedInputStream input, com.google.protobuf.ExtensionRegistryLite extensionRegistry)
                    throws java.io.IOException {
                com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry parsedMessage = null;
                try {
                    parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
                } catch (com.google.protobuf.InvalidProtocolBufferException e) {
                    parsedMessage = (com.mogujie.jarvis.protocol.ScheduleExpressionEntryProtos.ScheduleExpressionEntry) e.getUnfinishedMessage();
                    throw e;
                } finally {
                    if (parsedMessage != null) {
                        mergeFrom(parsedMessage);
                    }
                }
                return this;
            }

            private int bitField0_;

            private int expressionType_;

            /**
             * <code>required int32 expression_type = 1;</code>
             */
            @Override
            public boolean hasExpressionType() {
                return ((bitField0_ & 0x00000001) == 0x00000001);
            }

            /**
             * <code>required int32 expression_type = 1;</code>
             */
            @Override
            public int getExpressionType() {
                return expressionType_;
            }

            /**
             * <code>required int32 expression_type = 1;</code>
             */
            public Builder setExpressionType(int value) {
                bitField0_ |= 0x00000001;
                expressionType_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>required int32 expression_type = 1;</code>
             */
            public Builder clearExpressionType() {
                bitField0_ = (bitField0_ & ~0x00000001);
                expressionType_ = 0;
                onChanged();
                return this;
            }

            private java.lang.Object scheduleExpression_ = "";

            /**
             * <code>required string schedule_expression = 2;</code>
             */
            @Override
            public boolean hasScheduleExpression() {
                return ((bitField0_ & 0x00000002) == 0x00000002);
            }

            /**
             * <code>required string schedule_expression = 2;</code>
             */
            @Override
            public java.lang.String getScheduleExpression() {
                java.lang.Object ref = scheduleExpression_;
                if (!(ref instanceof java.lang.String)) {
                    com.google.protobuf.ByteString bs = (com.google.protobuf.ByteString) ref;
                    java.lang.String s = bs.toStringUtf8();
                    if (bs.isValidUtf8()) {
                        scheduleExpression_ = s;
                    }
                    return s;
                } else {
                    return (java.lang.String) ref;
                }
            }

            /**
             * <code>required string schedule_expression = 2;</code>
             */
            @Override
            public com.google.protobuf.ByteString getScheduleExpressionBytes() {
                java.lang.Object ref = scheduleExpression_;
                if (ref instanceof String) {
                    com.google.protobuf.ByteString b = com.google.protobuf.ByteString.copyFromUtf8((java.lang.String) ref);
                    scheduleExpression_ = b;
                    return b;
                } else {
                    return (com.google.protobuf.ByteString) ref;
                }
            }

            /**
             * <code>required string schedule_expression = 2;</code>
             */
            public Builder setScheduleExpression(java.lang.String value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                bitField0_ |= 0x00000002;
                scheduleExpression_ = value;
                onChanged();
                return this;
            }

            /**
             * <code>required string schedule_expression = 2;</code>
             */
            public Builder clearScheduleExpression() {
                bitField0_ = (bitField0_ & ~0x00000002);
                scheduleExpression_ = getDefaultInstance().getScheduleExpression();
                onChanged();
                return this;
            }

            /**
             * <code>required string schedule_expression = 2;</code>
             */
            public Builder setScheduleExpressionBytes(com.google.protobuf.ByteString value) {
                if (value == null) {
                    throw new NullPointerException();
                }
                bitField0_ |= 0x00000002;
                scheduleExpression_ = value;
                onChanged();
                return this;
            }

            // @@protoc_insertion_point(builder_scope:ScheduleExpressionEntry)
        }

        static {
            defaultInstance = new ScheduleExpressionEntry(true);
            defaultInstance.initFields();
        }

        // @@protoc_insertion_point(class_scope:ScheduleExpressionEntry)
    }

    private static final com.google.protobuf.Descriptors.Descriptor internal_static_ScheduleExpressionEntry_descriptor;
    private static com.google.protobuf.GeneratedMessage.FieldAccessorTable internal_static_ScheduleExpressionEntry_fieldAccessorTable;

    public static com.google.protobuf.Descriptors.FileDescriptor getDescriptor() {
        return descriptor;
    }

    private static com.google.protobuf.Descriptors.FileDescriptor descriptor;

    static {
        java.lang.String[] descriptorData = {
                "\n\037schedule_expression_entry.proto\"O\n\027Sch" + "eduleExpressionEntry\022\027\n\017expression_type\030"
                        + "\001 \002(\005\022\033\n\023schedule_expression\030\002 \002(\tB<\n\033co" + "m.mogujie.jarvis.protocolB\035ScheduleExpre"
                        + "ssionEntryProtos" };
        com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner = new com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner() {
            @Override
            public com.google.protobuf.ExtensionRegistry assignDescriptors(com.google.protobuf.Descriptors.FileDescriptor root) {
                descriptor = root;
                return null;
            }
        };
        com.google.protobuf.Descriptors.FileDescriptor.internalBuildGeneratedFileFrom(descriptorData,
                new com.google.protobuf.Descriptors.FileDescriptor[] {}, assigner);
        internal_static_ScheduleExpressionEntry_descriptor = getDescriptor().getMessageTypes().get(0);
        internal_static_ScheduleExpressionEntry_fieldAccessorTable = new com.google.protobuf.GeneratedMessage.FieldAccessorTable(
                internal_static_ScheduleExpressionEntry_descriptor, new java.lang.String[] { "ExpressionType", "ScheduleExpression", });
    }

    // @@protoc_insertion_point(outer_class_scope)
}
