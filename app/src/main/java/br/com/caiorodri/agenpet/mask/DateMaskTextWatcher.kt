package br.com.caiorodri.agenpet.mask

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import java.lang.ref.WeakReference

class DateMaskTextWatcher(editText: EditText) : TextWatcher {


    private val editTextReference: WeakReference<EditText> = WeakReference(editText)
    private var atualizando = false
    private var atual = ""

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

        val editText = editTextReference.get() ?: return;
        val cleanString = s.toString().replace("[^\\d]".toRegex(), "");

        if (atualizando || cleanString == atual) {
            return;
        }

        val finalCleanString = if (cleanString.length > 8) {
            cleanString.substring(0, 8);
        } else {
            cleanString
        }

        val formatted = StringBuilder();

        var i = 0;
        while (i < finalCleanString.length) {
            when (i) {
                2, 4 -> formatted.append("/");
            }
            formatted.append(finalCleanString[i]);
            i++;
        }

        atual = finalCleanString;
        atualizando = true;

        editText.setText(formatted.toString());
        editText.setSelection(formatted.length);

        atualizando = false;
    }

    override fun afterTextChanged(s: Editable?) {}

}