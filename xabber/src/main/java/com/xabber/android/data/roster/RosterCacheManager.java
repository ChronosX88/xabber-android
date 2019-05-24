package com.xabber.android.data.roster;

import android.util.Log;

import com.xabber.android.data.Application;
import com.xabber.android.data.account.AccountItem;
import com.xabber.android.data.account.AccountManager;
import com.xabber.android.data.database.RealmManager;
import com.xabber.android.data.database.messagerealm.MessageItem;
import com.xabber.android.data.database.realm.ContactRealm;
import com.xabber.android.data.entity.AccountJid;
import com.xabber.android.data.entity.UserJid;
import com.xabber.android.data.log.LogManager;
import com.xabber.android.data.message.AbstractChat;
import com.xabber.android.data.message.MessageManager;

import org.jxmpp.stringprep.XmppStringprepException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.realm.Realm;

public class RosterCacheManager {

    public static List<ContactRealm> loadContacts() {
        Realm realm = RealmManager.getInstance().getRealmUiThread();
        return realm.where(ContactRealm.class).findAll();
    }

//    public static List<RosterContact> loadContacts() {
//        Realm realm = RealmManager.getInstance().getRealmUiThread();
//        List<RosterContact> result = new ArrayList<>();
//        List<ContactRealm> contacts = realm.where(ContactRealm.class).findAll();
//        for (ContactRealm contactRealm : contacts) {
//            try {
//                AccountJid account = AccountJid.from(contactRealm.getAccount() + "/" + contactRealm.getAccountResource());
//                UserJid userJid = UserJid.from(contactRealm.getUser());
//                RosterContact contact = RosterContact.getRosterContact(account, userJid,
//                        contactRealm.getName());
//                result.add(contact);
//            } catch (UserJid.UserJidCreateException e) {
//                e.printStackTrace();
//            } catch (XmppStringprepException e) {
//                e.printStackTrace();
//            }
//        }
//        //realm.close();
//        return result;
//    }

    public static void saveContact(Collection<RosterContact> contacts) {
        Realm realm = RealmManager.getInstance().getNewBackgroundRealm();

        List<ContactRealm> newContacts = new ArrayList<>();
        for (RosterContact contact : contacts) {
            String account = contact.getAccount().getFullJid().asBareJid().toString();
            String user = contact.getUser().getBareJid().toString();

            ContactRealm contactRealm = new ContactRealm(account + "/" + user);
            contactRealm.setAccount(account);
            contactRealm.setUser(user);
            contactRealm.setName(contact.getName());
            contactRealm.setAccountResource(contact.getAccount().getFullJid().getResourcepart().toString());

            // это не совсем правильное место, для сохранения последних сообщений
            AbstractChat chat = MessageManager.getInstance().getChat(contact.getAccount(), contact.getUser());
            if (chat != null && chat.getLastMessage() != null) {
                Log.d("VALERA_TEST", "save OK");
                contactRealm.setLastMessage(chat.getLastMessage());
            }

            newContacts.add(contactRealm);
        }
        realm.beginTransaction();
        realm.copyToRealmOrUpdate(newContacts);
        realm.commitTransaction();
        realm.close();
    }

    public static void saveLastMessageToContact(Realm realm, MessageItem messageItem) {
        String account = messageItem.getAccount().getFullJid().asBareJid().toString();
        String user = messageItem.getUser().getBareJid().toString();
        ContactRealm contactRealm = realm.where(ContactRealm.class).equalTo(ContactRealm.Fields.ID, account + "/" + user).findFirst();
        if (contactRealm != null) {
            contactRealm.setLastMessage(messageItem);
            realm.copyToRealmOrUpdate(contactRealm);
        }
    }
}
