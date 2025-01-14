package jp.ac.jec.cm0120.pittan.database;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;


public class PittanSQLiteOpenHelper extends SQLiteOpenHelper {

  public static final String DB_NAME = "Pittan";
  public static final int DB_VERSION = 1;
  public static final String TABLE_PRODUCT = "product";
  public static final String TABLE_PLACE = "place";
  public static final String TABLE_PRODUCT_IMAGE = "product_image";

  public PittanSQLiteOpenHelper(@Nullable Context context) {
    super(context, DB_NAME, null, DB_VERSION);
  }

  @Override
  public void onCreate(SQLiteDatabase db) {

    /// Create tables
    String createProductTable = "CREATE TABLE " +
            TABLE_PRODUCT +
            "(product_id INTEGER PRIMARY KEY AUTOINCREMENT" +
            ", product_category TEXT" +
            ", product_color_code CAHR(6)" +
            ", product_design TEXT" +
            ", product_type TEXT" +
            ", product_comment TEXT" +
            ", product_recommended_size REAL" +
            ", product_height INTGER" +
            ", product_width INTGER)";

    String createProductImageTable = "CREATE TABLE " +
            TABLE_PRODUCT_IMAGE +
            "(product_image_id INTEGER PRIMARY KEY AUTOINCREMENT" +
            ", product_image_path TEXT" +
            ", product_id INTEGER" +
            ", FOREIGN KEY (product_id) REFERENCES " + TABLE_PRODUCT + "(product_id))";

    String createPlaceTable = "CREATE TABLE " +
            TABLE_PLACE +
            "(place_id INTEGER PRIMARY KEY AUTOINCREMENT" +
            ", place_name TEXT" +
            ", place_delete_flag TINYINT(1)" +
            ", product_id INTEGER" +
            ", FOREIGN KEY (product_id) REFERENCES " + TABLE_PRODUCT + "(product_id))";

    db.execSQL(createProductTable);
    db.execSQL(createProductImageTable);
    db.execSQL(createPlaceTable);

  }

  @Override
  public void onUpgrade(SQLiteDatabase db, int i, int i1) {

  }

  // region SQLite SELECT statement

  // Get Data Display In CardView
  public ArrayList<PittanProductDataModel> getSelectCardData() {
    ArrayList<PittanProductDataModel> arrayList = new ArrayList<>();
    String selectAllSql = "SELECT place_name,product_height,product_width,product_category,product_image_path,place_id FROM product " +
            "LEFT OUTER JOIN place ON product.product_id = place.product_id " +
            "LEFT OUTER JOIN product_image ON product.product_id = product_image.product_id " +
            "WHERE place_delete_flag = 0";

    SQLiteDatabase db = getReadableDatabase();
    if (db == null) {
      return null;
    }

    try {
      @SuppressLint("Recycle")
      Cursor cursor = db.rawQuery(selectAllSql, null);
      while (cursor.moveToNext()) {
        PittanProductDataModel temps = new PittanProductDataModel();
        temps.setPlaceName(cursor.getString(0));
        temps.setProductHeight(cursor.getInt(1));
        temps.setProductWidth(cursor.getInt(2));
        temps.setProductCategory(cursor.getString(3));
        temps.setProductImagePath(cursor.getString(4));
        temps.setPlaceID(cursor.getInt(5));

        arrayList.add(temps);
      }

    } catch (SQLiteException e) {
      e.printStackTrace();
    } finally {
      db.close();
    }

    return arrayList;
  }

  public ArrayList<PittanProductDataModel> getSelectDetailData(int placeID) {
    ArrayList<PittanProductDataModel> ary = new ArrayList<>();
    String selectDetailItemSql = "SELECT place_name,product_height,product_width,product_category,product_comment,product_image_path,place_id,place.product_id, product_image.product_id FROM product " +
            "LEFT OUTER JOIN place ON product.product_id = place.product_id " +
            "LEFT OUTER JOIN product_image ON product.product_id = product_image.product_id " +
            "WHERE place_id = " + placeID;

    SQLiteDatabase db = getReadableDatabase();
    if (db == null) {
      return null;
    }

    try {
      @SuppressLint("Recycle")
      Cursor cursor = db.rawQuery(selectDetailItemSql, null);
      while (cursor.moveToNext()) {
        PittanProductDataModel tmp = new PittanProductDataModel();
        tmp.setPlaceName(cursor.getString(0));
        tmp.setProductHeight(cursor.getInt(1));
        tmp.setProductWidth(cursor.getInt(2));
        tmp.setProductCategory(cursor.getString(3));
        tmp.setProductComment(cursor.getString(4));
        tmp.setProductImagePath(cursor.getString(5));
        tmp.setPlaceID(cursor.getInt(6));
        tmp.setProductID(cursor.getInt(7));
        ary.add(tmp);
      }
    } catch (SQLiteException e) {
      e.printStackTrace();
    } finally {
      db.close();
    }
    return ary;
  }

  // endregion

  //region SQLite INSERT statement
  // Insert　product TABLE
  public boolean insertProductData(PittanProductDataModel item) {
    ContentValues contentValues = new ContentValues();
    contentValues.put("product_category", item.getProductCategory());
    contentValues.put("product_comment", item.getProductComment());
    contentValues.put("product_height", item.getProductHeight());
    contentValues.put("product_width", item.getProductWidth());

    SQLiteDatabase db = getWritableDatabase();
    long ret;
    boolean isSuccess = false;
    try {
      ret = db.insert(TABLE_PRODUCT, null, contentValues);
      if (ret > 0) {
        isSuccess = isInsertPlaceData(item, ret);
        isSuccess = isInsertProductImageData(item, ret);
      }
    } catch (SQLiteException e) {
      e.printStackTrace();
    } finally {
      db.close();
    }
    return isSuccess;
  }

  // Insert place TABLE
  public boolean isInsertPlaceData(PittanProductDataModel item, long productID) {
    ContentValues contentValues = new ContentValues();
    contentValues.put("place_name", item.getPlaceName());
    contentValues.put("place_delete_flag", item.getPlaceDeleteFlag());
    contentValues.put("product_id", productID);

    SQLiteDatabase db = getWritableDatabase();
    long ret = -1;

    try {
      ret = db.insert(TABLE_PLACE, null, contentValues);
    } catch (SQLiteException e) {
      e.printStackTrace();
    } finally {
      db.close();
    }

    return ret > 0;
  }

  // Insert　product_image TABLE
  public boolean isInsertProductImageData(PittanProductDataModel item, long productID) {
    ContentValues contentValues = new ContentValues();
    contentValues.put("product_image_path", item.getProductImagePath());
    contentValues.put("product_id", productID);
    SQLiteDatabase db = getWritableDatabase();
    long ret = -1;

    try {
      ret = db.insert(TABLE_PRODUCT_IMAGE, null, contentValues);
    } catch (SQLiteException e) {
      e.printStackTrace();
    } finally {
      db.close();
    }

    return ret > 0;
  }

  // endregion

  /// region SQLite UPDATE statement

  public boolean isUpdatePlaceDeleteFlag(String placeID) {
    ContentValues contentValues = new ContentValues();
    contentValues.put("place_delete_flag", 1);

    SQLiteDatabase db = getWritableDatabase();
    long ret = -1;

    try {
      ret = db.update(TABLE_PLACE, contentValues, "place_id = " + placeID, null);
    } catch (SQLiteException e) {
      e.printStackTrace();
    } finally {
      db.close();
    }
    return ret > 0;
  }

  public boolean isUpdateProductDate(PittanProductDataModel model, int productId) {
    ContentValues contentValues = new ContentValues();

    if (model.getProductCategory().length() > 0) {
      contentValues.put("product_category", model.getProductCategory());
    }
    if (model.getProductHeight() > 0) {
      contentValues.put("product_height", model.getProductHeight());
    }
    if (model.getProductWidth() > 0) {
      contentValues.put("product_width", model.getProductWidth());
    }
    if (model.getProductComment().length() != 0) {
      contentValues.put("product_comment", model.getProductComment());
    }

    SQLiteDatabase db = getWritableDatabase();
    long ret = -1;

    try {
      ret = db.update(TABLE_PRODUCT, contentValues, "product_id = " + productId, null);
    } catch (SQLiteException e) {
      e.printStackTrace();
    } finally {
      db.close();
    }
    return ret > 0;
  }

  public boolean isUpdatePlaceData(PittanProductDataModel model, int placeId) {
    ContentValues contentValues = new ContentValues();
    contentValues.put("place_name", model.getPlaceName());

    SQLiteDatabase db = getWritableDatabase();
    long ret = -1;

    try {
      ret = db.update(TABLE_PLACE, contentValues, "place_id = " + placeId, null);
    } catch (SQLiteException e) {
      e.printStackTrace();
    } finally {
      db.close();
    }
    return ret > 0;
  }

  public boolean isUpdateProductImagePath(String productImageId, int productImagePath) {
    ContentValues contentValues = new ContentValues();
    contentValues.put("product_image_path", productImagePath);

    SQLiteDatabase db = getWritableDatabase();
    long ret = -1;

    try {
      ret = db.update(TABLE_PRODUCT_IMAGE, contentValues, "product_image_id = " + productImageId, null);
    } catch (SQLiteException e) {
      e.printStackTrace();
    } finally {
      db.close();
    }
    return ret > 0;
  }
  /// endregion
}
