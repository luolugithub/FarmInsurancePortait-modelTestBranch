package com.innovation.login;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.innovation.base.InnApplication;
import com.innovation.bean.LiPeiLocalBean;
import com.innovation.bean.company_child;
import com.innovation.bean.company_total;
import com.innovation.login.model.LocalModelNongxian;
import com.innovation.utils.HttpUtils;
import com.innovation.utils.PreferencesUtils;

import java.util.ArrayList;
import java.util.List;

import static com.innovation.base.InnApplication.ANIMAL_TYPE;

/**
 * Created by luolu on 08/01/2018.
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "UserManager.db";

    private static final String TABLE_USER = "user";
    private static final String TABLE_COMPANY = "tCompany";
    private static final String TABLE_LOCAL = "tLocal";
    private static final String TABLE_LIPEI = "tLipei";


    private static final String COLUMN_USER_ID = "user_id";
    private static final String COLUMN_USER_PHONE_NUMBER = "phone_number";
    private static final String COLUMN_USER_IDNUMBER = "user_idnumber";
    private static final String COLUMN_USER_PASSWORD = "user_password";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "fullname";
    private static final String COLUMN_PID = "pid";

    private String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "("
            + COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_USER_PHONE_NUMBER + " TEXT,"
            + COLUMN_USER_IDNUMBER + " TEXT," + COLUMN_USER_PASSWORD + " TEXT" + ")";

    private String DROP_USER_TABLE = "DROP TABLE IF EXISTS " + TABLE_USER;

    private String CREATE_COMPANY_TABLE = "CREATE TABLE " + TABLE_COMPANY + "("
            + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," + COLUMN_NAME + " TEXT,"
            + COLUMN_PID + " INTEGER)";

    private String DROP_COMPANY_TABLE = "DROP TABLE IF EXISTS " + TABLE_COMPANY;

    private String CREATE_LOCAL_TABLE = "CREATE TABLE " + TABLE_LOCAL + "(id integer primary key autoincrement,baodanNo text not null,name text not null,cardNo text not null,insureDate text not null,animalType text not null,yanBiaoName text not null,userid TEXT)";
    private String CREATE_LIPEI_TABLE = "CREATE TABLE " + TABLE_LIPEI + "(id integer primary key autoincrement,baodanNo text not null,insurename text not null,cardNo text not null,insureReason text not null,insureQSL text not null,insureDate text not null,longitude text not null,latitude text not null,animalType text not null,earsTagNo TEXT,zippath TEXT,recordeText TEXT,recordeMsg TEXT,userid TEXT,videozippath TEXT)";

    private String DROP_LOCAL_TABLE = "DROP TABLE IF EXISTS " + TABLE_LOCAL;
    private String DROP_LIPEI_TABLE = "DROP TABLE IF EXISTS " + TABLE_LIPEI;


    public DatabaseHelper(Context context) {

        super(context, DATABASE_NAME, null, HttpUtils.DATABSAE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_USER_TABLE);
        db.execSQL(CREATE_COMPANY_TABLE);
        db.execSQL(CREATE_LOCAL_TABLE);
        db.execSQL(CREATE_LIPEI_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.i("oldverson", oldVersion + "");
        Log.i("newverson", newVersion + "");
        db.execSQL(DROP_USER_TABLE);
        db.execSQL(DROP_COMPANY_TABLE);
        db.execSQL(DROP_LOCAL_TABLE);
        db.execSQL(DROP_LIPEI_TABLE);
        onCreate(db);
    }

    public void addLiPeiLocalData(LiPeiLocalBean liPeiLocalBean) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("baodanNo", liPeiLocalBean.pbaodanNo);
        values.put("insurename", liPeiLocalBean.pinsurename);
        values.put("cardNo", liPeiLocalBean.pcardNo);
        values.put("insureReason", liPeiLocalBean.pinsureReason);
        values.put("insureQSL", liPeiLocalBean.pinsureQSL);
        values.put("insureDate", liPeiLocalBean.pinsureDate);
        values.put("longitude", liPeiLocalBean.plongitude);
        values.put("latitude", liPeiLocalBean.platitude);
        values.put("animalType", liPeiLocalBean.panimalType);
        values.put("earsTagNo", liPeiLocalBean.earsTagNo);
        values.put("zippath", liPeiLocalBean.pzippath);
        values.put("videozippath", liPeiLocalBean.pVideozippath);
        values.put("recordeText", liPeiLocalBean.precordeText);
        values.put("userid", PreferencesUtils.getStringValue(HttpUtils.user_id, InnApplication.getAppContext()));
        db.insert(TABLE_LIPEI, null, values);
        db.close();
    }

    public List<LiPeiLocalBean> queryLocalDataFromLiPei(String muserid) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<LiPeiLocalBean> LiPeiLocalBeans = null;
        String[] columns = {"baodanNo", "insurename", "cardNo", "insureReason", "insureQSL", "insureDate", "longitude", "latitude", "animalType", "earsTagNo", "zippath", "recordeText", "recordeMsg","videozippath"};
        String selection = "userid=? and animalType=?";
        String[] selectionArgs = {muserid, String.valueOf(ANIMAL_TYPE)};
        Cursor cursor = db.query(TABLE_LIPEI, columns, selection, selectionArgs,
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            LiPeiLocalBeans = new ArrayList<LiPeiLocalBean>();
            while (cursor.moveToNext()) {
                String baodanNo = cursor.getString(cursor.getColumnIndex("baodanNo"));
                String insurename = cursor.getString(cursor.getColumnIndex("insurename"));
                String cardNo = cursor.getString(cursor.getColumnIndex("cardNo"));
                String insureReason = cursor.getString(cursor.getColumnIndex("insureReason"));
                String insureQSL = cursor.getString(cursor.getColumnIndex("insureQSL"));
                String insureDate = cursor.getString(cursor.getColumnIndex("insureDate"));
                String longitude = cursor.getString(cursor.getColumnIndex("longitude"));
                String latitude = cursor.getString(cursor.getColumnIndex("latitude"));
                String animalType = cursor.getString(cursor.getColumnIndex("animalType"));
                String earsTagNo = cursor.getString(cursor.getColumnIndex("earsTagNo"));
                String zippath = cursor.getString(cursor.getColumnIndex("zippath"));
                String recordeText = cursor.getString(cursor.getColumnIndex("recordeText"));
                String recordeMsg = cursor.getString(cursor.getColumnIndex("recordeMsg"));
                String videozippath = cursor.getString(cursor.getColumnIndex("videozippath"));
                LiPeiLocalBean localData = new LiPeiLocalBean(baodanNo, insurename, cardNo, insureReason, insureQSL, insureDate, longitude, latitude, animalType, earsTagNo, zippath, recordeText, recordeMsg,videozippath);
                LiPeiLocalBeans.add(localData);
            }
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        db.close();
        db = null;
        return LiPeiLocalBeans;
    }

    public boolean deleteLocalDataFromzippath(String path) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "zippath=?";
        String[] selectionArgs = {path};
        int delete = db.delete(TABLE_LIPEI, selection, selectionArgs);
        Log.i("===delete===", delete + "");
        db.close();
        return delete > 0 ? true : false;
    }

    public boolean deleteLocalDataFromdate(String date) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "insureDate=?";
        String[] selectionArgs = {date};
        int delete = db.delete(TABLE_LIPEI, selection, selectionArgs);
        Log.i("===delete===", delete + "");
        db.close();
        return delete > 0 ? true : false;
    }


    public int updateLiPeiLocalFromzipPath(String path, String lipeidate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("zippath", path);
        int update = db.update(TABLE_LIPEI, values, "insureDate=?", new String[]{lipeidate});
        db.close();
        return update;
    }
    public int updateLiPeiLocalFromVideozipPath(String path, String lipeidate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("videozippath", path);
        int update = db.update(TABLE_LIPEI, values, "insureDate=?", new String[]{lipeidate});
        db.close();
        return update;
    }
    public int updateLiPeiLocalFromrecordeText(String num, String lipeidate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recordeText", num);
        int update = db.update(TABLE_LIPEI, values, "insureDate=?", new String[]{lipeidate});
        db.close();
        return update;
    }

    public int updateLiPeiLocalFromrecordeMsg(String msg, String lipeidate) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("recordeMsg", msg);
        int update = db.update(TABLE_LIPEI, values, "insureDate=?", new String[]{lipeidate});
        db.close();
        return update;
    }

    public void addLocalNongxianData(LocalModelNongxian localData) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("baodanNo", localData.getBaodanNo());
        values.put("name", localData.getName());
        values.put("cardNo", localData.getCardNo());
        values.put("insureDate", localData.getInsureDate());
        values.put("animalType", localData.getType());
        values.put("yanBiaoName", localData.getYanBiaoName());
        values.put("userid", PreferencesUtils.getStringValue(HttpUtils.user_id, InnApplication.getAppContext()));
        db.insert(TABLE_LOCAL, null, values);
        db.close();
    }

    public List<LocalModelNongxian> queryLocalDataFromBaodanNo(String baodanNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<LocalModelNongxian> LocalModellist = null;
        String[] columns = {"baodanNo", "name", "cardNo", "insureDate", "animalType", "yanBiaoName"};
        String selection = "baodanNo=?";
        String[] selectionArgs = {baodanNo};
        Cursor cursor = db.query(TABLE_LOCAL, columns, selection, selectionArgs,
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            LocalModellist = new ArrayList<LocalModelNongxian>();
            while (cursor.moveToNext()) {
                String baodanNo1 = cursor.getString(cursor.getColumnIndex("baodanNo"));
                String name = cursor.getString(cursor.getColumnIndex("name"));
                String cardNo = cursor.getString(cursor.getColumnIndex("cardNo"));
                String insureDate = cursor.getString(cursor.getColumnIndex("insureDate"));
                String type = cursor.getString(cursor.getColumnIndex("animalType"));
                String yanBiaoName = cursor.getString(cursor.getColumnIndex("yanBiaoName"));
                LocalModelNongxian localData = new LocalModelNongxian(baodanNo1, name, cardNo, insureDate, type, yanBiaoName);
                LocalModellist.add(localData);
            }
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        db.close();
        db = null;
        return LocalModellist;
    }

    public boolean deleteLocalDataFromBaodanNo(String baodanNo) {
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = "baodanNo=?";
        String[] selectionArgs = {baodanNo};
        int delete = db.delete(TABLE_LOCAL, selection, selectionArgs);
        db.close();
        return delete > 0 ? true : false;
    }

    public List<LocalModelNongxian> queryLocalDatas(String muserid) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<LocalModelNongxian> listLocalDatas = new ArrayList<LocalModelNongxian>();
        if (db.isOpen()) {
            String[] columns = {"baodanNo", "name", "cardNo", "insureDate", "yanBiaoName"};
            Cursor cursor = db.query(TABLE_LOCAL, columns, "userid=? and animalType=?", new String[]{muserid, String.valueOf(ANIMAL_TYPE)}, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    String baodanNo = cursor.getString(cursor.getColumnIndex("baodanNo"));
                    String name = cursor.getString(cursor.getColumnIndex("name"));
                    String cardNo = cursor.getString(cursor.getColumnIndex("cardNo"));
                    String insureDate = cursor.getString(cursor.getColumnIndex("insureDate"));
                    String yanBiaoName = cursor.getString(cursor.getColumnIndex("yanBiaoName"));
                    LocalModelNongxian localData = new LocalModelNongxian(baodanNo, name, cardNo, insureDate, String.valueOf(ANIMAL_TYPE), yanBiaoName);
                    listLocalDatas.add(localData);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return listLocalDatas;
    }

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_USER_PHONE_NUMBER, user.getPhoneNumber());
        values.put(COLUMN_USER_IDNUMBER, user.getIDNumber());
        values.put(COLUMN_USER_PASSWORD, user.getPassword());

        db.insert(TABLE_USER, null, values);
        db.close();
    }

    public void addCompany(List<String> sqls) {
//        SQLiteDatabase db = this.getWritableDatabase();
//        ContentValues values = new ContentValues();
//        values.put(COLUMN_ID, company.getCompanyId());
//        values.put(COLUMN_NAME, company.getCompanyName());
//        values.put(COLUMN_PID, company.getCompanyPid());
//
//        db.insert(TABLE_COMPANY, null, values);
//        db.close();

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();
        try {
            for (String sql : sqls) {
                db.execSQL(sql);
            }
// 设置事务标志为成功，当结束事务时就会提交事务
            db.setTransactionSuccessful();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
// 结束事务
            db.endTransaction();
            db.close();
        }
    }

    public boolean checkUser(String phoneNumber, String password) {
        String[] columns = {COLUMN_USER_ID};
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USER_PHONE_NUMBER + " = ?" + " AND " + COLUMN_USER_PASSWORD + " = ? ";
        String[] selectionArgs = {phoneNumber, password};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public boolean checkUser(String phone_number) {
        String[] columns = {COLUMN_USER_ID};
        SQLiteDatabase db = this.getWritableDatabase();
        String selection = COLUMN_USER_PHONE_NUMBER + " = ?";
        String[] selectionArgs = {phone_number};

        Cursor cursor = db.query(TABLE_USER, columns, selection, selectionArgs, null, null, null);
        int cursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if (cursorCount > 0) {
            return true;
        }
        return false;
    }

    public List<company_total> queryProvince() {
        SQLiteDatabase db = this.getWritableDatabase();
        List<company_total> listProvinces = new ArrayList<company_total>();
        if (db.isOpen()) {
            String[] columns = {"id", "fullname"};

            String selection = "pid=?";
            String[] selectionArgs = {"0"};
            Cursor cursor = db.query("tCompany", columns, selection,
                    selectionArgs, null, null, null);
            if (cursor != null && cursor.getCount() > 0) {
                while (cursor.moveToNext()) {
                    int sCode = cursor.getInt(0);
                    String provinceName = cursor.getString(1);
                    company_total province = new company_total(sCode, provinceName);
                    listProvinces.add(province);
                }
            }
            if (cursor != null) {
                cursor.close();
            }
        }
        db.close();
        return listProvinces;
    }

    public List<company_child> queryCity(int code) {
        SQLiteDatabase db = this.getWritableDatabase();
        List<company_child> cityList = null;
        String[] columns = {"id", "fullname"};

        String selection = "pid=?";
        String[] selectionArgs = {String.valueOf(code)};
        Cursor cursor = db.query("tCompany", columns, selection, selectionArgs,
                null, null, null);
        if (cursor != null && cursor.getCount() > 0) {
            cityList = new ArrayList<company_child>();
            while (cursor.moveToNext()) {
                int sCode = cursor.getInt(0);
                String cName = cursor.getString(1);
                company_child city = new company_child();
                city.setCompanyId(sCode);
                city.setCompanyName(cName);
                cityList.add(city);
            }
        }
        if (cursor != null) {
            cursor.close();
            cursor = null;
        }
        db.close();
        db = null;
        return cityList;
    }
}
