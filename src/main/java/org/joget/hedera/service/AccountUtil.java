package org.joget.hedera.service;

import com.hedera.hashgraph.sdk.AccountId;
import com.hedera.hashgraph.sdk.AccountInfo;
import com.hedera.hashgraph.sdk.AccountInfoQuery;
import com.hedera.hashgraph.sdk.BadMnemonicException;
import com.hedera.hashgraph.sdk.Client;
import com.hedera.hashgraph.sdk.Mnemonic;
import com.hedera.hashgraph.sdk.PrivateKey;
import com.hedera.hashgraph.sdk.PublicKey;
import org.joget.apps.form.service.FormUtil;
import org.joget.commons.util.LogUtil;

public class AccountUtil {
    
    private AccountUtil() {}
    
    public static boolean isAccountExist(Client client, String accountId) {
        //If within a Form Builder, don't make useless API calls
        if (accountId == null || accountId.isBlank() || FormUtil.isFormBuilderActive()) {
            return false;
        }
        
        try {
            AccountInfo accountInfo = new AccountInfoQuery()
                .setAccountId(AccountId.fromString(accountId))
                .execute(client);
            
            return (accountInfo != null);
        } catch (Exception ex) {
            //Ignore if not successful.
        }
        
        return false;
    }
    
    public static PrivateKey derivePrivateKeyFromMnemonic(Mnemonic mnemonic) {
        try {
            return mnemonic.toPrivateKey();
        } catch (BadMnemonicException ex) {
            LogUtil.error(PluginUtil.class.getName(), ex, "Unable to derive key from mnemonic phrase. Reason: " + ex.reason.toString());
            return null;
        }
    }
    
    public static PublicKey derivePublicKeyFromMnemonic(Mnemonic mnemonic) {
        return derivePrivateKeyFromMnemonic(mnemonic).getPublicKey();
    }
}
