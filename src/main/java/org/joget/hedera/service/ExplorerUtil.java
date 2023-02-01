package org.joget.hedera.service;

import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.TransactionId;
import com.hedera.hashgraph.sdk.TransactionRecord;
import com.hedera.hashgraph.sdk.TransactionRecordQuery;
import java.time.Instant;
import java.util.Map;
import org.joget.commons.util.LogUtil;
import static org.joget.hedera.service.BackendUtil.MAINNET_NAME;
import static org.joget.hedera.service.BackendUtil.PREVIEWNET_NAME;
import static org.joget.hedera.service.BackendUtil.TESTNET_NAME;

public class ExplorerUtil {
    
    private ExplorerUtil() {}
    
    private static final String HASHSCAN_TYPE = "hashscan";
    private static final String DRAGONGLASS_TYPE = "dragonglass";
    
    //Hedera explorer links
    private static final String HASHSCAN_MAINNET = "https://hashscan.io/mainnet/";
    private static final String HASHSCAN_TESTNET = "https://hashscan.io/testnet/";
    private static final String HASHSCAN_PREVIEWNET = "https://hashscan.io/previewnet/";
    
    private static final String DRAGONGLASS_MAINNET = "https://app.dragonglass.me/";
    private static final String DRAGONGLASS_TESTNET = "https://testnet.dragonglass.me/";
    private static final String DRAGONGLASS_PREVIEWNET = ""; //Not available

    private static String getHashscanUrl(String networkType) {
        switch (networkType) {
            case MAINNET_NAME:
                return HASHSCAN_MAINNET;
            case TESTNET_NAME:
                return HASHSCAN_TESTNET;
            case PREVIEWNET_NAME:
                return HASHSCAN_PREVIEWNET;
            default:
                LogUtil.warn(ExplorerUtil.class.getName(), "Unknown network selection found!");
                return null;
        }
    }
    
    private static String getDragonglassUrl(String networkType) {
        switch (networkType) {
            case MAINNET_NAME:
                return DRAGONGLASS_MAINNET;
            case TESTNET_NAME:
                return DRAGONGLASS_TESTNET;
            case PREVIEWNET_NAME:
                return DRAGONGLASS_PREVIEWNET;
            default:
                LogUtil.warn(ExplorerUtil.class.getName(), "Unknown network selection found!");
                return null;
        }
    }
    
    public static String getTransactionUrl(Client client, Map properties, String txId, String explorerType) {
        TransactionRecord txRecord;
        
        try {
            txRecord = new TransactionRecordQuery()
                    .setTransactionId(TransactionId.fromString(txId))
                    .execute(client);
        } catch (Exception ex) {
            LogUtil.warn(ExplorerUtil.class.getName(), "Unable to retrieve transaction record...");
            return null;
        }
        
        return getTransactionUrl(properties, txRecord, explorerType);
    }
    
    //Default is Hashscan for all transaction URLs
    public static String getTransactionUrl(Map properties, TransactionRecord txRecord) {
        return getTransactionUrl(properties, txRecord, HASHSCAN_TYPE);
    }
    
    public static String getTransactionUrl(Map properties, TransactionRecord txRecord, String explorerType) {
        String transactionId = txRecord.transactionId.toString();
        
        //No need to return immediately, in case user wants to show link as is
        if (transactionId == null || transactionId.isBlank()) {
            transactionId = "";
        }

        String networkType = BackendUtil.getNetworkType(properties);
        
        switch (explorerType) {
            case DRAGONGLASS_TYPE: {
                String formattedTxId = transactionId.replaceAll("[^0-9]","");
                return getDragonglassUrl(networkType) + "transactions/" + formattedTxId;
            }
            case HASHSCAN_TYPE:
            default:
                Instant consensusTimestamp = txRecord.consensusTimestamp;
                String formattedTimestamp = String.valueOf(consensusTimestamp.getEpochSecond()) + "." + String.valueOf(consensusTimestamp.getNano());
                String formattedTxId = transactionId.replaceAll("@", "-");
                formattedTxId = formattedTxId.substring(0, formattedTxId.lastIndexOf(".")) + "-" + formattedTxId.substring(formattedTxId.lastIndexOf(".") + 1);
                return getHashscanUrl(networkType) + "transaction/" + formattedTimestamp + "?tid=" + formattedTxId;
        }
    }
    
    public static String getAddressUrl(Map properties, String accountAddress, String explorerType) {
        //No need to return immediately, in case user wants to show link as is
        if (accountAddress == null || accountAddress.isBlank()) {
            accountAddress = "";
        }
        
        String networkType = BackendUtil.getNetworkType(properties);
        
        switch (explorerType) {
            case DRAGONGLASS_TYPE:
                return getDragonglassUrl(networkType) + "accounts/" + accountAddress;
            case HASHSCAN_TYPE:
            default:
                return getHashscanUrl(networkType) + "account/" + accountAddress;
        }
    }
    
    public static String getTokenUrl(Map properties, String tokenId, String explorerType) {
        //No need to return immediately, in case user wants to show link as is
        if (tokenId == null || tokenId.isBlank()) {
            tokenId = "";
        }
        
        String networkType = BackendUtil.getNetworkType(properties);
        
        switch (explorerType) {
            case DRAGONGLASS_TYPE:
                return getDragonglassUrl(networkType) + "tokens/" + tokenId;
            case HASHSCAN_TYPE:
            default:
                return getHashscanUrl(networkType) + "token/" + tokenId;
        }
    }
}
