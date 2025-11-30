package br.com.caiorodri.agenpet.utils

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

class MaskTextWatcher(private val editText: EditText, private val mask: String) : TextWatcher {
    private var isUpdating = false;
    private var old = "";

    override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {

        val str = unmask(s.toString());
        var mascara = "";

        if (isUpdating) {
            old = str;
            isUpdating = false;
            return;
        }

        var finalStr = str;
        if (count == 0 && str.length == old.length && str.isNotEmpty()) {
            finalStr = str.substring(0, str.length - 1);
        }

        var i = 0;
        for (m in mask.toCharArray()) {
            if (m != '#') {
                if (i < finalStr.length) {
                    mascara += m;
                }
                continue;
            }

            try {
                mascara += finalStr[i];
            } catch (e: Exception) {
                break;
            }
            i++;
        }

        isUpdating = true;

        editText.setText(mascara);

        try {
            editText.setSelection(mascara.length);
        } catch (e: Exception) { }
    }

    override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
    override fun afterTextChanged(s: Editable) {}

    companion object {
        fun unmask(s: String): String {
            return s.replace("[.]".toRegex(), "").replace("[-]".toRegex(), "")
                .replace("[/]".toRegex(), "").replace("[(]".toRegex(), "")
                .replace("[)]".toRegex(), "").replace(" ".toRegex(), "")
        }
    }
}