package io.yggdrash.core.net;

import io.yggdrash.PeerTestUtils;
import io.yggdrash.TestConstants;
import io.yggdrash.core.blockchain.BranchId;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class PeerTableGroupTest {

    private PeerTableGroup peerTableGroup;
    private BranchId yggdrash;

    @Before
    public void setUp() {
        KademliaOptions.BUCKET_SIZE = 5;
        yggdrash = TestConstants.yggdrash();
        peerTableGroup = PeerTestUtils.createTableGroup();
        peerTableGroup.createTable(yggdrash);
    }

    /*
    6 peers will be returned after selfRefresh
    11 peers are existed on peerTable including peer and owner
    */
    @Test
    public void selfRefresh() {
        peerTableGroup.selfRefresh();
        assertEquals(7, peerTableGroup.getPeerTable(yggdrash).getAllPeerAddressList().size());
    }

    /*
    Try refresh with TARGET
    The size of closestPeers will be 0, so selfRefresh will be executed.
    selfLookup is done after loadSeedNodes.
    lookup will proceed with the target after selfRefresh done.
    A total of 13 peers will be existed in the bucket which includes 6 peers received after lookup,
    5 peers received after lookup by target, owner, and seed.
     */
    @Test
    public void refresh() {
        peerTableGroup.refresh();
        assertEquals(12, peerTableGroup.getPeerTable(yggdrash).getAllPeerAddressList().size());
    }
}