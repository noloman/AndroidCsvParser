package com.nulltwenty.rabobankcsvparser

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel

/**
 * Returns a [List<CsvFileModel] as content of the original CSV file:
 *
 * "First name","Sur name","Issue count","Date of birth","avatar"
"Theo","Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png"
 */
val originalCsvFileModelList: List<CsvFileModel> = listOf(
    CsvFileModel(
        "Theo",
        "Jansen",
        5,
        "1978-01-02T00:00:00",
        "https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
    ), CsvFileModel(
        "Fiona",
        "de Vries",
        7,
        "1950-11-12T00:00:00",
        "https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
    ), CsvFileModel(
        "Petra",
        "Boersma",
        1,
        "2001-04-20T00:00:00",
        "https://api.multiavatar.com/2672c49d6099f87274.png"
    )
)

val malformedDateCsvFileModelList: List<CsvFileModel> = listOf(
    CsvFileModel(
        "Theo",
        "Jansen",
        5,
        "1978-01-0200:00:00",
        "https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
    ), CsvFileModel(
        "Fiona",
        "de Vries",
        7,
        "19-110:00",
        "https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
    ), CsvFileModel(
        "Petra",
        "Boersma",
        1,
        "4-20T00:00:00",
        "https://api.multiavatar.com/2672c49d6099f87274.png"
    )
)

const val originalCsvString = """"First name","Sur name","Issue count","Date of birth","avatar"
"Theo","Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png""""

const val missingQuotesInHeaderString =
    """First name","Sur name","Issue count","Date of birth","avatar"
"Theo","Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png""""

const val missingOneQuoteInHeaderString =
    """First name","Sur name","Issue count","Date of birth","avatar"
"Theo""Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png""""

const val missingOneCommaToSeparateFirstNameAndSurNameString =
    """"First name","Sur name","Issue count","Date of birth","avatar"
"Theo""Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png""""

const val missingOneComaInHeaderString = """"First name""Sur name","Issue count","Date of birth","avatar"
"Theo","Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png""""

const val malformedDateString = """"First name","Sur name","Issue count","Date of birth","avatar"
"Theo","Jansen",5,"1978-01-0200:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"19-110:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"4-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png""""