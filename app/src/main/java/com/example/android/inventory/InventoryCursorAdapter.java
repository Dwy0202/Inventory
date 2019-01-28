package com.example.android.inventory;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.data.InventoryContract.InventoryEntry;

public class InventoryCursorAdapter extends CursorAdapter {

    /**
     * Constructs a new {@link InventoryCursorAdapter}.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        // Inflate a list item view using the layout specified in list_item.xml
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * This method binds the product data (in the current row pointed to by cursor) to the given
     * list item layout. For example, the name for the current product can be set on the name TextView
     * in the list item layout.
     *
     * @param view    Existing view, returned earlier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the
     *                correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {

        final Button saleButton = view.findViewById(R.id.sale_button);
        int currentId = cursor.getInt(cursor.getColumnIndex(InventoryEntry._ID));
        final Uri contentUri = Uri.withAppendedPath(InventoryEntry.CONTENT_URI, Integer.toString(currentId));

        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.name);
        TextView priceTextView = (TextView) view.findViewById(R.id.price);
        final TextView quantityTextView = (TextView) view.findViewById(R.id.quantity);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
        final int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        String price = cursor.getString(priceColumnIndex);
        String quantity = cursor.getString(quantityColumnIndex);


        //Set the TextViews with the attributes from the cursor
        nameTextView.setText(String.valueOf(productName));

        if (price.equals("0")) {
            price = context.getString(R.string.price_unknown);
            priceTextView.setText(String.valueOf(price));

        } else {
            priceTextView.setText(String.valueOf(price));
        }

        if (quantity.equals("0")) {
            quantityTextView.setText(R.string.quantity_out_of_stock);

        } else {
            quantityTextView.setText(String.valueOf(quantity));
        }


        //Sale button that decreased quantity by 1 and updates the database
        saleButton.setOnClickListener(new View.OnClickListener() {
            ContentValues values = new ContentValues();
            int quantityIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int quantity = cursor.getInt(quantityIndex);

            @Override
            public void onClick(View v) {

                Log.i("Quantity before sale", String.valueOf(quantity));
                int sale = 1;

                if (quantity > 0) {
                    quantity = quantity - sale;
                    quantityTextView.setText(String.valueOf(quantity));
                    values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantity);
                    context.getContentResolver().update(contentUri, values, null, null);

                    Log.i("Quantity after sale", String.valueOf(quantity));
                    Toast.makeText(context, R.string.successful_sale, Toast.LENGTH_SHORT).show();

                } else if (quantity == 0) {
                    quantityTextView.setText(R.string.quantity_out_of_stock);
                    Toast.makeText(context, R.string.click_add_product, Toast.LENGTH_SHORT).show();

                }
            }
        });
    }
}

