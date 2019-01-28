package com.example.android.inventory;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.data.InventoryContract.InventoryEntry;

public class EditorActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_INVENTORY_LOADER = 0;
    public String supplierNumber;
    public EditText supplierNumberEditText;
    private Uri mCurrentInventoryUri;
    private EditText mInventoryNameEditText;
    private EditText mInventoryPriceEditText;
    private EditText mInventoryQuantityEditText;
    private EditText mInventorySupplierEditText;
    private EditText mInventorySupplierNumberEditText;
    private boolean mInventoryHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            mInventoryHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle saveInstanceState) {
        super.onCreate(saveInstanceState);
        setContentView(R.layout.activity_editor);

        Intent intent = getIntent();
        mCurrentInventoryUri = intent.getData();

        if (mCurrentInventoryUri == null) {
            setTitle(getString(R.string.add_new_product));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.edit_a_product));
        }

        getLoaderManager().initLoader(EXISTING_INVENTORY_LOADER, null, this);


        mInventoryNameEditText = (EditText) findViewById(R.id.edit_product_name);
        mInventoryPriceEditText = (EditText) findViewById(R.id.edit_product_price);
        mInventoryQuantityEditText = (EditText) findViewById(R.id.edit_product_quantity);
        mInventorySupplierEditText = (EditText) findViewById(R.id.edit_product_supplier);
        mInventorySupplierNumberEditText = (EditText) findViewById(R.id.edit_product_supplier_number);


        mInventoryNameEditText.setOnTouchListener(mTouchListener);
        mInventoryPriceEditText.setOnTouchListener(mTouchListener);
        mInventoryQuantityEditText.setOnTouchListener(mTouchListener);
        mInventorySupplierEditText.setOnTouchListener(mTouchListener);
        mInventorySupplierNumberEditText.setOnTouchListener(mTouchListener);

    }


    private void saveInventory() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = mInventoryNameEditText.getText().toString().trim();
        String priceString = mInventoryPriceEditText.getText().toString().trim();
        String quantityString = mInventoryQuantityEditText.getText().toString().trim();
        String supplierString = mInventorySupplierEditText.getText().toString().trim();
        String supplierNumberString = mInventorySupplierNumberEditText.getText().toString().trim();


        // Check if this is supposed to be a new product
        // and check if all the fields in the editor are blank
        if (mCurrentInventoryUri != null && TextUtils.isEmpty(nameString)
                || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(supplierString)
                || TextUtils.isEmpty(supplierNumberString)) {
            // Since no fields were modified, we can return early without creating a new pet.
            // No need to create ContentValues and no need to do any ContentProvider operations.

            Toast.makeText(this, getString(R.string.editor_save_product), Toast.LENGTH_LONG).show();

            return;
        }

        // Create a ContentValues object where column names are the keys,
        // and pet attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_PRODUCT_NAME, nameString);
        values.put(InventoryEntry.COLUMN_PRODUCT_PRICE, priceString);
        values.put(InventoryEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER, supplierString);
        values.put(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER, supplierNumberString);


        // Determine if this is a new or existing pet by checking if mCurrentPetUri is null or not
        if (mCurrentInventoryUri == null) {
            // This is a NEW pet, so insert a new pet into the provider,
            // returning the content URI for the new pet.
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        // Otherwise this is an EXISTING product, so update the product with content URI: mCurrentInventoryUri
        // and pass in the new ContentValues. Pass in null for the selection and selection args
        // because mCurrentInventoryUri will already identify the correct row in the database that
        // we want to modify.
        else {
            getContentResolver().update(mCurrentInventoryUri, values, null, null);
        }
        // Show a toast message depending on whether or not the update was successful.
        if (mCurrentInventoryUri != null && TextUtils.isEmpty(nameString)
                || TextUtils.isEmpty(priceString)
                || TextUtils.isEmpty(quantityString)
                || TextUtils.isEmpty(supplierString)
                || TextUtils.isEmpty(supplierNumberString)) {
            // If no rows were affected, then there was an error with the update.
            Toast.makeText(this, getString(R.string.editor_update_product_failed),
                    Toast.LENGTH_SHORT).show();
        } else {
            // Otherwise, the update was successful and we can display a toast.
            Toast.makeText(this, getString(R.string.editor_update_product_successful),
                    Toast.LENGTH_SHORT).show();
        }
        finish();
    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentInventoryUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Save product to database
                saveInventory();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // If the pet hasn't changed, continue with navigating up to parent activity
                // which is the {@link CatalogActivity}.
                if (!mInventoryHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    return true;
                }

                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                // User clicked "Discard" button, navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(EditorActivity.this);
                            }
                        };

                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mInventoryHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked "Discard" button, close the current activity.
                        finish();
                    }
                };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }


    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        if (mCurrentInventoryUri == null) {
            return null;
        }

        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_PRODUCT_NAME,
                InventoryEntry.COLUMN_PRODUCT_QUANTITY,
                InventoryEntry.COLUMN_PRODUCT_PRICE,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER,
                InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER};

        return new CursorLoader(this,
                mCurrentInventoryUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {
            // Find the columns of pet attributes that we're interested in
            int nameColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER);
            int supplierNumberColumnIndex = cursor.getColumnIndex(InventoryEntry.COLUMN_PRODUCT_SUPPLIER_NUMBER);

            // Extract out the value from the Cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            String quantity = cursor.getString(quantityColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            String supplier = cursor.getString(supplierColumnIndex);
            String supplierNumber = cursor.getString(supplierNumberColumnIndex);

            // Update the views on the screen with the values from the database
            mInventoryNameEditText.setText(name);
            mInventoryPriceEditText.setText(Integer.toString(price));
            mInventoryQuantityEditText.setText(quantity);
            mInventorySupplierEditText.setText(supplier);
            mInventorySupplierNumberEditText.setText(supplierNumber);

        }

        Button increaseQuantityButton = findViewById(R.id.quantity_increase_button);

        increaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(mInventoryQuantityEditText.getText().toString());

                if (quantity >= 0) {
                    quantity = quantity + 1;
                }
                mInventoryQuantityEditText.setText(Integer.toString(quantity));
            }
        });

        Button decreaseQuantityButton = findViewById(R.id.quantity_decrease_button);

        decreaseQuantityButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = Integer.valueOf(mInventoryQuantityEditText.getText().toString());

                if (quantity >= 1) {
                    quantity = quantity - 1;
                }
                mInventoryQuantityEditText.setText(Integer.toString(quantity));

            }
        });

        Button orderButton = findViewById(R.id.order_button);

        supplierNumberEditText = findViewById(R.id.edit_product_supplier_number);
        orderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                supplierNumber = supplierNumberEditText.getText().toString();
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + supplierNumber));
                startActivity(callIntent);
            }
        });

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        //if the loader is invalidated, clear out all the data from the input fields.
        mInventoryNameEditText.setText("");
        mInventoryQuantityEditText.setText("");
        mInventoryPriceEditText.setText("");
        mInventorySupplierEditText.setText("");
        mInventorySupplierNumberEditText.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        if (mCurrentInventoryUri != null) {

            int rowsDeleted = getContentResolver().delete(mCurrentInventoryUri, null, null);

            if (rowsDeleted == 0) {

                Toast.makeText(this, getString(R.string.editor_delete_product_failed), Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful), Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }


}
