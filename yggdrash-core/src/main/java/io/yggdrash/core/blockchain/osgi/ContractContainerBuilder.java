package io.yggdrash.core.blockchain.osgi;

import io.yggdrash.core.store.StateStore;
import io.yggdrash.core.store.TransactionReceiptStore;
import org.osgi.framework.launch.FrameworkFactory;

import java.util.Map;

public class ContractContainerBuilder {
    private FrameworkFactory frameworkFactory;
    private Map<String, String> containerConfig;
    private String branchId;
    private StateStore stateStore;
    private TransactionReceiptStore transactionReceiptStore;

    private ContractContainerBuilder() {

    }

    public static ContractContainerBuilder newInstance() {
        return new ContractContainerBuilder();
    }

    public ContractContainerBuilder withFrameworkFactory(FrameworkFactory frameworkFactory) {
        this.frameworkFactory = frameworkFactory;
        return this;
    }

    public ContractContainerBuilder withContainerConfig(Map<String, String> containerConfig) {
        this.containerConfig = containerConfig;
        return this;
    }

    public ContractContainerBuilder withBranchId(String branchId) {
        this.branchId = branchId;
        return this;
    }

    public ContractContainerBuilder withStateStore(StateStore stateStore) {
        this.stateStore = stateStore;
        return this;
    }

    public ContractContainerBuilder withTransactionReceiptStore(TransactionReceiptStore transactionReceiptStore) {
        this.transactionReceiptStore = transactionReceiptStore;
        return this;
    }

    public ContractContainer build() {
        if (this.frameworkFactory == null) {
            throw new IllegalStateException("Must set frameworkFactory");
        }

        if (this.containerConfig == null) {
            throw new IllegalStateException("Must set commonContainerConfig");
        }

        if (this.branchId == null) {
            throw new IllegalStateException("Must set branchId");
        }

        ContractContainer contractContainer = new ContractContainer(
                this.frameworkFactory
                , this.containerConfig
                , this.branchId
                , this.stateStore
                , this.transactionReceiptStore
        );
        contractContainer.newFramework();
        return contractContainer;
    }
}
