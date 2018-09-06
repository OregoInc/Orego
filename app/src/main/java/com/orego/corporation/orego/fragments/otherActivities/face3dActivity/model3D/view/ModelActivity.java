package com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.view;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;

import com.orego.corporation.orego.fragments.otherActivities.face3dActivity.model3D.portrait.headComposition.HeadComposition;
import com.orego.corporation.orego.utils.ClientMultipartFormPost;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;


public final class ModelActivity extends Activity {

    private ModelSurfaceView gLView;
    private String countModel;
    private ModelActivity THIS = this;
    private HeadComposition headComposition;

    @SuppressLint("ResourceAsColor")
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final Bundle b = getIntent().getExtras();
        this.countModel = String.valueOf(b.getInt("countModel"));
        new AsynchronLoader().execute();
    }

    public String getCountModel() {
        return countModel;
    }

    public File getDirectory(){
        return new File(new File(getCacheDir(), "/OREGO")
                , "/directory" + getCountModel());
    }

    class AsynchronLoader extends AsyncTask<Void, Integer, Void> {
        private ProgressDialog dialog;


        AsynchronLoader() {
            dialog = new ProgressDialog(THIS);
        }

        @Override
        protected final void onPreExecute() {
            super.onPreExecute();
            this.dialog.setMessage("Loading...");
            this.dialog.setCancelable(false);
            this.dialog.show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            publishProgress(0);
            try {
                File file = new File(getDirectory(), "resultObj.buf");
                publishProgress(1);
                if (!file.exists()) {
                    file = ClientMultipartFormPost.sendFile(getDirectory());
                }
                publishProgress(2);
                InputStream isFace = new FileInputStream(file);
                headComposition = new HeadComposition(isFace);
                publishProgress(3);

                runOnUiThread(() -> {
                    gLView = new ModelSurfaceView(THIS, headComposition);
                    setContentView(gLView);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            switch (values[0]) {
                case 0:
                    this.dialog.setMessage("Готовим Ваше фото");
                    break;
                case 1:
                    this.dialog.setMessage("Делаем магию с Вашей фотографией");
                    break;
                case 2:
                    this.dialog.setMessage("Рисуем ваше чудо-личико :)");
                    break;
                case 3:
                    this.dialog.setMessage("Готово");
                    break;
            }
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
        }
    }

}
