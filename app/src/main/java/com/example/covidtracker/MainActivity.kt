package com.example.covidtracker

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Display.Mode
import android.view.View
import android.widget.*
import android.widget.Adapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.hbb20.CountryCodePicker
import org.eazegraph.lib.charts.PieChart
import org.eazegraph.lib.models.PieModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.create
import java.text.NumberFormat
import java.util.Locale.filter

class MainActivity : AppCompatActivity() {
    var mPieChart: PieChart? = null
    var countryCodePicker: CountryCodePicker? = null
    var mTodayTotal: TextView? = null
    var mTotal: TextView? = null
    var mActive: TextView? = null
    var mTodayActive: TextView? = null
    var mRecovered: TextView? = null
    var mTodayRecovered: TextView? = null
    var mDeaths: TextView? = null
    var mTodayDeaths: TextView? = null
    var mFilter: TextView? = null
    var country: String? = null
    var mSpinner: Spinner? = null
    var types: ArrayList<String> = arrayListOf("cases","deaths","recovered","active")
    var modelClassList: ArrayList<ModelClass>? = null
    var modelClassList2: ArrayList<ModelClass>? = null
    var recyclerView: RecyclerView? = null
    var adapter: com.example.covidtracker.Adapter? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        initialize()



        // Creating an ArrayAdapter and setting it as the adapter for the spinner.
        if(mSpinner != null){
            val arrayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, types)
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            mSpinner?.adapter = arrayAdapter

            mSpinner?.onItemSelectedListener = object :
                AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    var item: String = types[position]
                    mFilter?.text = item
                    adapter?.filter(item)

                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    // write code to perform some action
                }
        }


        // This is a retrofit call to the api.
        var apiInterface: ApiInterface? = RetrofitInstance().getRetrofit()?.create(ApiInterface::class.java)
        apiInterface?.getCountryData()?.enqueue(object: Callback<List<ModelClass>>{
            override fun onResponse(
                call: Call<List<ModelClass>>,
                response: Response<List<ModelClass>>) {
                if(response.body() != null){
                    response.body()?.let { modelClassList2!!.addAll(it) }
                    adapter?.notifyDataSetChanged()
                }
                else{
                    Toast.makeText(this@MainActivity, "List is empty", Toast.LENGTH_SHORT).show()
                }

            }
            override fun onFailure(call: Call<List<ModelClass>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "OnCreate"+t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })


        // creating a recycler view and setting the adapter to it.
        adapter = Adapter(modelClassList2!!)
        recyclerView?.layoutManager = LinearLayoutManager(this)
        recyclerView?.setHasFixedSize(true)
        recyclerView?.adapter = adapter


        /* This is a listener which is used to detect the country of the user and then fetch the data
        of that country. */
        countryCodePicker?.setAutoDetectedCountry(true)
        country = countryCodePicker?.selectedCountryName
        countryCodePicker?.setOnCountryChangeListener {
            country = countryCodePicker?.selectedCountryName
            fetchData()
        }
    }

}

    private fun initialize() {
        countryCodePicker = findViewById(R.id.ccp)
        mTodayActive = findViewById(R.id.todayactive)
        mActive = findViewById(R.id.activecases)
        mDeaths = findViewById(R.id.totaldeath)
        mTodayDeaths = findViewById(R.id.todaydeath)
        mRecovered = findViewById(R.id.recoveredcase)
        mTodayRecovered = findViewById(R.id.todayrecovered)
        mTotal = findViewById(R.id.totalcase)
        mTodayTotal = findViewById(R.id.todaytotal)
        mPieChart = findViewById(R.id.piechart)
        mSpinner = findViewById(R.id.spinner)
        mFilter = findViewById(R.id.filter)
        recyclerView = findViewById(R.id.recyclerview)
        modelClassList = ArrayList<ModelClass>()
        modelClassList2 = ArrayList<ModelClass>()
    }

    private fun fetchData() {
        var apiInterface: ApiInterface? = RetrofitInstance().getRetrofit()?.create(ApiInterface::class.java)
        apiInterface?.getCountryData()?.enqueue(object: Callback<List<ModelClass>>{
            override fun onResponse(call: Call<List<ModelClass>>, response: Response<List<ModelClass>>) {
                response.body()?.let { modelClassList?.addAll(it) }
                for(i in 0 until modelClassList!!.size-1){
                    if(modelClassList!![i].country == country){

                        mActive?.setText(modelClassList!![i].active)
                        mDeaths?.setText(modelClassList!![i].deaths)
                        mTodayDeaths?.setText(modelClassList!![i].todayDeaths)
                        mRecovered?.setText(modelClassList!![i].recovered)
                        mTodayRecovered?.setText(modelClassList!![i].todayRecovered)
                        mTodayTotal?.setText(modelClassList!![i].todayCases)
                        mTotal?.setText(modelClassList!![i].cases)



                        var active: Int = modelClassList!![i].active.toInt()
                        var total: Int = modelClassList!![i].cases.toInt()
                        var recovered: Int = modelClassList!![i].recovered.toInt()
                        var deaths: Int = modelClassList!![i].deaths.toInt()

                        updateGraph(active,total,recovered,deaths)
                    }
                }
            }

            override fun onFailure(call: Call<List<ModelClass>>, t: Throwable) {
                Toast.makeText(this@MainActivity, "Other"+t.localizedMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateGraph(active: Int, total: Int, recovered: Int, deaths: Int) {
        mPieChart?.clearChart()
        mPieChart?.addPieSlice(PieModel("Confirm", total.toFloat(), Color.parseColor("#FFB701")))
        mPieChart?.addPieSlice(PieModel("Active", active.toFloat(), Color.parseColor("#FF4CAF50")))
        mPieChart?.addPieSlice(PieModel("Recovered", recovered.toFloat(), Color.parseColor("#38ACCD")))
        mPieChart?.addPieSlice(PieModel("Deaths", deaths.toFloat(), Color.parseColor("#F55c47")))
        mPieChart?.startAnimation()
    }
    }
