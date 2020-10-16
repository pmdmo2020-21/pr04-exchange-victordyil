package es.iessaladillo.pedrojoya.exchange

import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import es.iessaladillo.pedrojoya.exchange.databinding.MainActivityBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: MainActivityBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = MainActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupViews()
    }

    private fun setupViews() {
        binding.lblAmount.selectAll()
        listenToMe()
        binding.lblAmount.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE && !binding.lblAmount.text.endsWith(".")) {
                change()
                hideSoftKeyboard(binding.lblAmount)
                true
            } else {
                false
            }
        }
    }

    private fun hideSoftKeyboard(view: View): Boolean {
        val imm = view.context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
        return imm.hideSoftInputFromWindow(view.windowToken, 0)

    }

    private fun changeImage(image: ImageView, dolar: Currency) {
        image.setImageResource(dolar.drawableResId)
    }

    private fun listenToMe() {

        binding.lblAmount.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (s.isEmpty() || s.startsWith(".")) {
                    binding.lblAmount.setText(getString(R.string.initialValue))
                    binding.lblAmount.selectAll()
                }
            }

            override fun beforeTextChanged(
                s: CharSequence, start: Int,
                count: Int, after: Int
            ) {

            }

            override fun onTextChanged(
                s: CharSequence, start: Int,
                before: Int, count: Int
            ) {
            }
        })
        binding.FRadioButtom.setOnCheckedChangeListener { _, checkedId ->
            currencySelector(
                checkedId
            )
        }
        binding.TRadioButtom.setOnCheckedChangeListener { _, checkedId ->
            currencySelector(
                checkedId
            )
        }
        binding.button.setOnClickListener { if (!binding.lblAmount.text.endsWith(".")) change() }

    }


    private fun currencySelector(checkid: Int) {
        when (checkid) {
            binding.FDolar.id -> {
                changeImage(binding.FCurrencyImage, Currency.DOLLAR)
                disable(D = false, E = true, P = true, isFrom = true)
                binding.TDolar.isChecked = false
                binding.TEuro.isChecked = true
            }
            binding.FEuro.id -> {
                changeImage(binding.FCurrencyImage, Currency.EURO)
                disable(D = true, E = false, P = true, isFrom = true)
                binding.TEuro.isChecked = false
            }
            binding.FPound.id -> {
                changeImage(binding.FCurrencyImage, Currency.POUND)
                disable(D = true, E = true, P = false, isFrom = true)
                binding.TPound.isChecked = false
            }
            binding.TDolar.id -> {
                changeImage(binding.TCurrencyImage, Currency.DOLLAR)
                disable(D = false, E = true, P = true, isFrom = false)
                binding.FDolar.isChecked = false
            }
            binding.TEuro.id -> {
                changeImage(binding.TCurrencyImage, Currency.EURO)
                disable(D = true, E = false, P = true, isFrom = false)
                binding.FEuro.isChecked = false
            }
            binding.TPound.id -> {
                changeImage(binding.TCurrencyImage, Currency.POUND)
                disable(D = true, E = true, P = false, isFrom = false)
                binding.FPound.isChecked = false
            }


        }

    }

    private fun disable(D: Boolean, E: Boolean, P: Boolean, isFrom: Boolean) {
        if (isFrom) {
            binding.TDolar.isEnabled = D
            binding.TEuro.isEnabled = E
            binding.TPound.isEnabled = P
        } else {
            binding.FDolar.isEnabled = D
            binding.FEuro.isEnabled = E
            binding.FPound.isEnabled = P
        }
    }

    private fun change() {

        lateinit var symbol1: String
        lateinit var symbol2: String
        val amount = binding.lblAmount.text.toString().toDouble()
        val result = when (binding.FRadioButtom.checkedRadioButtonId) {
            binding.FDolar.id -> {
                symbol1 = Currency.DOLLAR.symbol
                when (binding.TRadioButtom.checkedRadioButtonId) {
                    binding.TEuro.id -> {
                        symbol2 = Currency.EURO.symbol
                        Currency.EURO.fromDollar(amount)
                    }
                    binding.TPound.id -> {
                        symbol2 = Currency.POUND.symbol
                        Currency.POUND.fromDollar(amount)
                    }
                    else -> 0.00
                }
            }
            binding.FEuro.id -> {
                when (binding.TRadioButtom.checkedRadioButtonId) {
                    binding.TDolar.id -> Currency.EURO.toDollar(amount)
                    binding.TPound.id -> Currency.POUND.fromDollar(Currency.EURO.toDollar(amount))
                    else -> 0.00
                }
            }
            binding.FPound.id -> {
                when (binding.TRadioButtom.checkedRadioButtonId) {
                    binding.TDolar.id -> Currency.POUND.toDollar(amount)
                    binding.TEuro.id -> Currency.EURO.fromDollar(Currency.POUND.toDollar(amount))
                    else -> 0.00
                }
            }
            else -> 0.00
        }
        Toast.makeText(
            this,
            getString(R.string.result, amount, symbol1, result, symbol2),
            Toast.LENGTH_SHORT
        ).show()
    }
}