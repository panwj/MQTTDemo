package com.mavl.im.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.mavl.im.entity.Message;
import com.mavl.im.sdk.util.Logger;


import java.util.ArrayList;

public class DaoUtil2 {

    public static void saveMessage(Context context, Message imMessage) {
        try {
            if (imMessage == null) return;
            SQLiteDatabase db = DaoManager.getInstance(context).getWritableDatabase();
            db.beginTransaction();
            try {
                String sql = "INSERT INTO " + DaoConstants.MessageEntry2.DATABASE_TABLE_MESSAGES
                        + "("
                        + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_ID + ", "
                        + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_CLIENT_ID + ", "
                        + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_PAYLOAD + ", "
                        + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_TIMESTAMP + ", "
                        + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_STATUS + ", "
                        + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_IS_RECEIVED + ", "
                        + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_FROM_UID + ", "
                        + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_TO_UID
                        + ") VALUES "
                        + "('"
                        + imMessage.messageId + "',"
                        + imMessage.messageLocalId + ",'"
                        + imMessage.payload.replaceAll("'", "''") + "',"//避免名称中的单引号，导致sql语法错误。
                        + imMessage.timeStamp + ","
                        + imMessage.status + ","
                        + (imMessage.isReceived ? 1 : 0) + ",'"
                        + imMessage.fromUid + "','"
                        + imMessage.toUid
                        + "')";
                db.execSQL(sql);
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("updateMessage() save exception : " + e.toString());
            } finally {
                db.endTransaction();
                DaoManager.getInstance(context).closeDatabase();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("updateMessage() exception : " + e.toString());
        }
    }

    public static void saveMessage(Context context, ArrayList<Message> list) {
        try {
            if (list == null || list.size() <= 0) return;
            SQLiteDatabase db = DaoManager.getInstance(context).getWritableDatabase();
            db.beginTransaction();
            try {
                for (Message imMessage : list) {
                    String sql = "INSERT INTO " + DaoConstants.MessageEntry2.DATABASE_TABLE_MESSAGES
                            + "("
                            + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_ID + ", "
                            + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_CLIENT_ID + ", "
                            + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_PAYLOAD + ", "
                            + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_TIMESTAMP + ", "
                            + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_STATUS + ", "
                            + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_IS_RECEIVED + ", "
                            + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_FROM_UID + ", "
                            + DaoConstants.MessageEntry2.COLUMNS_MESSAGE_TO_UID
                            + ") VALUES "
                            + "('"
                            + imMessage.messageId + "',"
                            + imMessage.messageLocalId + ",'"
                            + imMessage.payload.replaceAll("'", "''") + "',"//避免名称中的单引号，导致sql语法错误。
                            + imMessage.timeStamp + ","
                            + imMessage.status + ","
                            + (imMessage.isReceived ? 1 : 0) + ",'"
                            + imMessage.fromUid + "','"
                            + imMessage.toUid
                            + "')";
                    db.execSQL(sql);
                }
                db.setTransactionSuccessful();
            } catch (Exception e) {
                e.printStackTrace();
                Logger.e("updateMessage() save exception : " + e.toString());
            } finally {
                db.endTransaction();
                DaoManager.getInstance(context).closeDatabase();
            }
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("updateMessage() exception : " + e.toString());
        }
    }

    public static void updateMessage(Context context, Message message) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_ID, message.messageId);
            contentValues.put(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_IS_RECEIVED, message.isReceived ? 1 : 0);
            contentValues.put(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_STATUS, message.status);
            contentValues.put(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_TIMESTAMP, message.timeStamp);
            contentValues.put(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_FROM_UID, message.fromUid);
            contentValues.put(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_TO_UID, message.toUid);

            String whereClause = DaoConstants.MessageEntry2.COLUMNS_MESSAGE_CLIENT_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(message.messageLocalId)};

            DaoManager.getInstance(context).getWritableDatabase().update(DaoConstants.MessageEntry2.DATABASE_TABLE_MESSAGES,
                    contentValues, whereClause, whereArgs);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("updateMessage() exception = " + e.toString());
        } finally {
            DaoManager.getInstance(context).closeDatabase();
        }
    }

    public static void updateMessageStatus(Context context, Message message) {
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_STATUS, message.status);

            String whereClause = DaoConstants.MessageEntry2.COLUMNS_MESSAGE_CLIENT_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(message.messageLocalId)};

            DaoManager.getInstance(context).getWritableDatabase().update(DaoConstants.MessageEntry2.DATABASE_TABLE_MESSAGES,
                    contentValues, whereClause, whereArgs);

        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("updateMessage() exception = " + e.toString());
        } finally {
            DaoManager.getInstance(context).closeDatabase();
        }
    }

    public static ArrayList<Message> getMessageList(Context context) {
        ArrayList<Message> list = new ArrayList<>();
        Cursor cursor = null;
        try {
            cursor = DaoManager.getInstance(context).getReadableDatabase().query(DaoConstants.MessageEntry2.DATABASE_TABLE_MESSAGES, null, null, null, null, null, DaoConstants.MessageEntry2.COLUMNS_MESSAGE_TIMESTAMP + " ASC");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    Message message = getMessageFromCursor(cursor);
                    list.add(message);
                    cursor.moveToNext();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            DaoManager.getInstance(context).closeDatabase();
        }
        return list;
    }

    public static Message getIMMessageByClientId(Context context, int msgClientId) {
        Message object = null;
        Cursor cursor = null;
        try {
            String whereClause = DaoConstants.MessageEntry2.COLUMNS_MESSAGE_CLIENT_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(msgClientId)};

            cursor = DaoManager.getInstance(context).getReadableDatabase().query(DaoConstants.MessageEntry2.DATABASE_TABLE_MESSAGES, null, whereClause, whereArgs, null, null, DaoConstants.MessageEntry2.COLUMNS_MESSAGE_CLIENT_ID + " ASC");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    object = getMessageFromCursor(cursor);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            DaoManager.getInstance(context).closeDatabase();
        }
        return object;
    }

    public static Message getIMMessageByServerId(Context context, String msgServerId) {
        Message object = null;
        Cursor cursor = null;
        try {
            String whereClause = DaoConstants.MessageEntry2.COLUMNS_MESSAGE_ID + "=?";
            String[] whereArgs = new String[]{msgServerId};

            cursor = DaoManager.getInstance(context).getReadableDatabase().query(DaoConstants.MessageEntry2.DATABASE_TABLE_MESSAGES, null, whereClause, whereArgs, null, null, DaoConstants.MessageEntry2.COLUMNS_MESSAGE_ID + " ASC");
            if (cursor != null && cursor.getCount() > 0) {
                cursor.moveToFirst();
                while (!cursor.isAfterLast()) {
                    object = getMessageFromCursor(cursor);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (cursor != null) cursor.close();
            DaoManager.getInstance(context).closeDatabase();
        }
        return object;
    }

    public static boolean deleteIMMessageByClientId(Context context, int msgClientId) {
        try {
            String whereClause = DaoConstants.MessageEntry2.COLUMNS_MESSAGE_CLIENT_ID + "=?";
            String[] whereArgs = new String[]{String.valueOf(msgClientId)};
            return delete(context, DaoConstants.MessageEntry2.DATABASE_TABLE_MESSAGES, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("deleteIMMessageByClientId() exception = " + e.toString());
        }
        return false;
    }

    public static boolean deleteIMMessageByServerId(Context context, String msgServerId) {
        try {
            String whereClause = DaoConstants.MessageEntry2.COLUMNS_MESSAGE_ID + "=?";
            String[] whereArgs = new String[]{msgServerId};
            return delete(context, DaoConstants.MessageEntry2.DATABASE_TABLE_MESSAGES, whereClause, whereArgs);
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("deleteIMMessageByServerId() exception = " + e.toString());
        }
        return false;
    }

    public static boolean delete(Context context, String table, String whereClause, String[] whereArgs) {
        DaoManager daoManager = DaoManager.getInstance(context);
        try {
            int raw = daoManager.getWritableDatabase().delete(table, whereClause, whereArgs);
            return raw > 0 ? true : false;
        } catch (Exception e) {
            e.printStackTrace();
            Logger.e("delete() exception : " + e.toString());
        } finally {
            daoManager.closeDatabase();
        }
        return false;
    }

    private static Message getMessageFromCursor(Cursor cursor) {
        Message message = new Message();
        String id = cursor.getString(cursor.getColumnIndexOrThrow(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_ID));
        int clientId = cursor.getInt(cursor.getColumnIndexOrThrow(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_CLIENT_ID));
        String payload = cursor.getString(cursor.getColumnIndexOrThrow(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_PAYLOAD));
        long timeStamp = cursor.getLong(cursor.getColumnIndexOrThrow(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_TIMESTAMP));
        int status = cursor.getInt(cursor.getColumnIndexOrThrow(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_STATUS));
        int isReceived = cursor.getInt(cursor.getColumnIndexOrThrow(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_IS_RECEIVED));
        String fromUid = cursor.getString(cursor.getColumnIndexOrThrow(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_FROM_UID));
        String toUid = cursor.getString(cursor.getColumnIndexOrThrow(DaoConstants.MessageEntry2.COLUMNS_MESSAGE_TO_UID));

        message.messageId = id;
        message.messageLocalId = clientId;
        message.payload = payload;
        message.timeStamp = timeStamp;
        message.status = status;
        message.isReceived = (isReceived == 1 ? true : false);
        message.fromUid = fromUid;
        message.toUid = toUid;

        return message;
    }
}
