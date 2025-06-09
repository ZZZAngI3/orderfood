package com.example.orderfood.database;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;
import com.example.orderfood.entity.Dish;
import com.example.orderfood.entity.User;
import com.example.orderfood.entity.CartItem;

@Database(entities = {Dish.class, User.class, CartItem.class}, version = 3, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    private static AppDatabase instance;

    public abstract DishDao dishDao();
    public abstract UserDao userDao();
    public abstract CartDao cartDao();

    public static synchronized AppDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(),
                            AppDatabase.class, "order_food_db")
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .addCallback(roomDatabaseCallback)
                    .build();
        }
        return instance;
    }

    // 数据库创建和打开时的回调
    private static RoomDatabase.Callback roomDatabaseCallback = new RoomDatabase.Callback() {
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            // 可以在这里进行数据库初始化操作，例如插入初始数据
        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
        }
    };

    // 示例迁移：从版本1迁移到版本2，为 Dish 表添加 stock 字段
    static final Migration MIGRATION_1_2 = new Migration(1, 2) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 为 Dish 表添加 stock 字段，并设置默认值为 0
            database.execSQL("ALTER TABLE dish ADD COLUMN stock INTEGER NOT NULL DEFAULT 0");
        }
    };

    // 从版本2迁移到版本3，为 cart_item 表添加 stock 字段
    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            // 为 cart_item 表添加 stock 字段，并设置默认值为 0
            database.execSQL("ALTER TABLE cart_item ADD COLUMN stock INTEGER NOT NULL DEFAULT 0");
        }
    };
}