package com.sparkingpathshala.app.data.local

object QuestionBank {
    val questions = mapOf(
        // Class 6 Mathematics
        "Class 6_Math" to listOf(
            QuestionEntity("m6_01", "practice_Class 6_Math", "What is the successor of 999?", "1000", "998", "1001", "1002", 0, "Successor is obtained by adding 1: 999 + 1 = 1000."),
            QuestionEntity("m6_02", "practice_Class 6_Math", "Which is the smallest whole number?", "0", "1", "-1", "None of these", 0, "Whole numbers start from 0, so 0 is the smallest whole number."),
            QuestionEntity("m6_03", "practice_Class 6_Math", "The perimeter of a square with side 5 cm is:", "20 cm", "25 cm", "15 cm", "10 cm", 0, "Perimeter = 4 * side = 4 * 5 = 20 cm."),
            QuestionEntity("m6_04", "practice_Class 6_Math", "What fraction of an hour is 40 minutes?", "2/3", "1/2", "3/4", "1/3", 0, "40 minutes / 60 minutes = 2/3."),
            QuestionEntity("m6_05", "practice_Class 6_Math", "LCM of 4 and 6 is:", "12", "24", "8", "6", 0, "Multiples of 4: 4, 8, 12, 16... Multiples of 6: 6, 12, 18... Smallest common is 12."),
            QuestionEntity("m6_06", "practice_Class 6_Math", "An angle whose measure is less than 90 degrees is called:", "Acute angle", "Obtuse angle", "Right angle", "Straight angle", 0, "An angle smaller than 90 degrees is an acute angle."),
            QuestionEntity("m6_07", "practice_Class 6_Math", "What is the additive identity for integers?", "0", "1", "-1", "None", 0, "Adding 0 to any integer keeps the integer unchanged."),
            QuestionEntity("m6_08", "practice_Class 6_Math", "What is the Roman numeral for 29?", "XXIX", "XIX", "XXXI", "XXVII", 0, "XXIX represents 10 + 10 + (10 - 1) = 29."),
            QuestionEntity("m6_09", "practice_Class 6_Math", "How many factors does 12 have?", "6", "4", "5", "8", 0, "Factors of 12 are 1, 2, 3, 4, 6, 12. Total 6."),
            QuestionEntity("m6_10", "practice_Class 6_Math", "Which of the following is the smallest prime number?", "2", "1", "3", "0", 0, "2 is the smallest prime number and the only even prime."),
            QuestionEntity("m6_11", "practice_Class 6_Math", "1 million is equal to how many lakhs?", "10 lakhs", "1 lakh", "100 lakhs", "5 lakhs", 0, "1 million = 1,000,000, which is 10 lakhs in Indian system."),
            QuestionEntity("m6_12", "practice_Class 6_Math", "Find the perimeter of an equilateral triangle of side 4 cm.", "12 cm", "16 cm", "8 cm", "6 cm", 0, "Perimeter of equilateral triangle = 3 * side = 3 * 4 = 12 cm."),
            QuestionEntity("m6_13", "practice_Class 6_Math", "What is the decimal notation for 7/10?", "0.7", "0.07", "7.0", "0.77", 0, "7 divided by 10 is 0.7."),
            QuestionEntity("m6_14", "practice_Class 6_Math", "Find the area of a rectangle with length 6 cm and width 4 cm.", "24 sq cm", "20 sq cm", "10 sq cm", "12 sq cm", 0, "Area of rectangle = length * width = 6 * 4 = 24 sq cm."),
            QuestionEntity("m6_15", "practice_Class 6_Math", "How many lines of symmetry does a regular hexagon have?", "6", "3", "4", "8", 0, "A regular hexagon has 6 lines of symmetry corresponding to its vertices and sides.")
        ),
        // Class 8 Mathematics
        "Class 8_Math" to listOf(
            QuestionEntity("m8_01", "practice_Class 8_Math", "The multiplicative inverse of -5/9 is:", "-9/5", "9/5", "5/9", "1", 0, "Multiplicative inverse of a/b is b/a. So for -5/9 it is -9/5."),
            QuestionEntity("m8_02", "practice_Class 8_Math", "What is the square of 15?", "225", "125", "625", "250", 0, "15 * 15 = 225."),
            QuestionEntity("m8_03", "practice_Class 8_Math", "If 3x + 2 = 11, then x is:", "3", "4", "2", "5", 0, "3x = 11 - 2 = 9, so x = 9/3 = 3."),
            QuestionEntity("m8_04", "practice_Class 8_Math", "How many diagonals does a regular hexagon have?", "9", "6", "5", "4", 0, "Diagonals = n(n-3)/2 = 6(3)/2 = 9."),
            QuestionEntity("m8_05", "practice_Class 8_Math", "The cube root of 512 is:", "8", "6", "12", "16", 0, "8 * 8 * 8 = 512."),
            QuestionEntity("m8_06", "practice_Class 8_Math", "Factorization of x^2 - 9 is:", "(x-3)(x+3)", "(x-3)(x-3)", "(x+3)(x+3)", "x(x-9)", 0, "Using standard identity a^2 - b^2 = (a-b)(a+b), x^2 - 3^2 = (x-3)(x+3)."),
            QuestionEntity("m8_07", "practice_Class 8_Math", "If x/3 = 5, what is the value of x?", "15", "8", "5/3", "2", 0, "Multiply both sides by 3: x = 5 * 3 = 15."),
            QuestionEntity("m8_08", "practice_Class 8_Math", "Find the sum of all interior angles of a quadrilateral.", "360 degrees", "180 degrees", "540 degrees", "720 degrees", 0, "Interior angle sum for polygon = (n-2)*180. For n=4, (4-2)*180 = 360."),
            QuestionEntity("m8_09", "practice_Class 8_Math", "What is the square root of 625?", "25", "15", "35", "45", 0, "25 * 25 = 625."),
            QuestionEntity("m8_10", "practice_Class 8_Math", "Express 0.000035 in standard scientific notation form.", "3.5 * 10^-5", "3.5 * 10^-4", "35 * 10^-6", "0.35 * 10^-4", 0, "Move decimal point 5 places to the right: 3.5 * 10^-5."),
            QuestionEntity("m8_11", "practice_Class 8_Math", "What is the formula for the area of a trapezium?", "1/2 * (a+b) * h", "a * b * h", "1/2 * a * b", "2 * (a+b) * h", 0, "Area of trapezium = half of sum of parallel sides times altitude."),
            QuestionEntity("m8_12", "practice_Class 8_Math", "What is the multiplicative identity for rational numbers?", "1", "0", "-1", "None", 0, "Multiplying any rational number by 1 leaves it unchanged."),
            QuestionEntity("m8_13", "practice_Class 8_Math", "What is the value of 5^-2?", "1/25", "25", "-25", "-1/25", 0, "5^-2 = 1/(5^2) = 1/25."),
            QuestionEntity("m8_14", "practice_Class 8_Math", "According to Euler's formula for polyhedrons, F + V - E is equal to:", "2", "0", "1", "3", 0, "Euler's formula states: Faces + Vertices - Edges = 2."),
            QuestionEntity("m8_15", "practice_Class 8_Math", "What is the class mark of the class interval 10 - 20?", "15", "10", "20", "30", 0, "Class mark = (Upper Limit + Lower Limit) / 2 = (20 + 10) / 2 = 15.")
        ),
        // Class 8 Science (CBSE 2026 NCERT)
        "Class 8_Science" to listOf(
            QuestionEntity("c8_sci01", "practice_Class 8_Science", "Which of the following is a Kharif crop grown during the rainy season?", "Paddy (Rice)", "Wheat", "Gram", "Mustard", 0, "Paddy requires substantial water and is sown during the monsoon (June-September), making it a Kharif crop."),
            QuestionEntity("c8_sci02", "practice_Class 8_Science", "Which bacterium helps in the formation of curd from milk?", "Lactobacillus", "Rhizobium", "Spirogyra", "Yeast", 0, "Lactobacillus promotes the curdling of milk by fermenting lactose into lactic acid."),
            QuestionEntity("c8_sci03", "practice_Class 8_Science", "The process of conversion of sugar into alcohol by yeast is known as:", "Fermentation", "Pasteurisation", "Combustion", "Sedimentation", 0, "Fermentation is the biological breakdown of sugars into ethanol and CO2 discovered by Louis Pasteur in 1857."),
            QuestionEntity("c8_sci04", "practice_Class 8_Science", "Which of the following is an exhaustible fossil fuel that is also known as 'Black Gold'?", "Petroleum", "Biogas", "Wood", "Charcoal", 0, "Petroleum is called Black Gold due to its high commercial and industrial value."),
            QuestionEntity("c8_sci05", "practice_Class 8_Science", "Which zone of a candle flame is the hottest and is used by goldsmiths?", "Outer non-luminous blue zone", "Middle luminous yellow zone", "Innermost dark zone", "Bottom wick zone", 0, "The outermost blue zone undergoes complete combustion and is the hottest part of the flame."),
            QuestionEntity("c8_sci06", "practice_Class 8_Science", "The Red Data Book is a source book that keeps a record of all:", "Endangered plants and animals", "Extinct volcanoes", "National parks", "Medicinal plants", 0, "The Red Data Book maintained by IUCN catalogues endangered species of flora and fauna."),
            QuestionEntity("c8_sci07", "practice_Class 8_Science", "In humans, the fertilisation of an egg by a sperm takes place in the:", "Oviduct (Fallopian tube)", "Uterus", "Ovary", "Cervix", 0, "Fertilisation occurs inside the female oviduct/fallopian tube when sperm meets the ovum."),
            QuestionEntity("c8_sci08", "practice_Class 8_Science", "Which endocrine gland controls physical changes during adolescence and is known as the master gland?", "Pituitary gland", "Thyroid gland", "Adrenal gland", "Pancreas", 0, "The pituitary gland secretes hormones that stimulate sex hormones and overall adolescent growth."),
            QuestionEntity("c8_sci09", "practice_Class 8_Science", "What is the force acting per unit area of a surface called?", "Pressure", "Friction", "Tension", "Gravity", 0, "Pressure is defined as Force divided by Area (P = F / A, measured in Pascals)."),
            QuestionEntity("c8_sci10", "practice_Class 8_Science", "Which type of friction is always the smallest among the following?", "Rolling friction", "Static friction", "Sliding friction", "Fluid friction", 0, "Rolling friction is much smaller than sliding or static friction, which is why ball bearings and wheels reduce friction."),
            QuestionEntity("c8_sci11", "practice_Class 8_Science", "The frequency of sound is measured in which SI unit?", "Hertz (Hz)", "Decibel (dB)", "Newton (N)", "Metre/second (m/s)", 0, "Frequency is cycles per second, measured in Hertz (Hz). Loudness is measured in decibels."),
            QuestionEntity("c8_sci12", "practice_Class 8_Science", "The process of depositing a thin protective layer of any desired metal on another material using electricity is called:", "Electroplating", "Electrolysis", "Electrorefining", "Galvanisation", 0, "Electroplating uses electric current to coat objects with metals like chromium, gold, or zinc."),
            QuestionEntity("c8_sci13", "practice_Class 8_Science", "What instrument is used to detect whether an object is carrying an electric charge or not?", "Electroscope", "Periscope", "Kaleidoscope", "Stethoscope", 0, "A gold-leaf electroscope detects electric charge through repulsion of like charges."),
            QuestionEntity("c8_sci14", "practice_Class 8_Science", "The angle of incidence is always equal to the angle of reflection according to:", "First law of reflection", "Second law of reflection", "Snell's law", "Law of conservation of energy", 0, "According to the laws of reflection, Angle of Incidence (i) = Angle of Reflection (r)."),
            QuestionEntity("c8_sci15", "practice_Class 8_Science", "Which cell organelle is called the control center of the cell?", "Nucleus", "Cytoplasm", "Vacuole", "Mitochondria", 0, "The nucleus contains genetic material (DNA/chromosomes) and directs all cellular activities.")
        ),
        // Class 8 English (CBSE 2026 Honeydew & Grammar)
        "Class 8_English" to listOf(
            QuestionEntity("c8_eng01", "practice_Class 8_English", "In the story 'The Best Christmas Present in the World', what was the gift Connie received?", "A letter from her husband Jim Macpherson", "A gold wristwatch", "A box of chocolates", "A photo album", 0, "Connie believed the narrator was Jim returning with his wartime Christmas letter."),
            QuestionEntity("c8_eng02", "practice_Class 8_English", "Who wrote the poignant poem 'The Ant and the Cricket'?", "Aesop (Aesop's Fables)", "Robert Frost", "William Blake", "Rabindranath Tagore", 0, "The fable in verse is adapted from Aesop's classic fables depicting foresight versus laziness."),
            QuestionEntity("c8_eng03", "practice_Class 8_English", "In the chapter 'A Visit to Cambridge', whom did Firdaus Kanga meet and interview?", "Stephen Hawking", "Albert Einstein", "Issac Newton", "Carl Sagan", 0, "Firdaus Kanga visited the world-renowned astrophysicist Stephen Hawking at Cambridge."),
            QuestionEntity("c8_eng04", "practice_Class 8_English", "Choose the correct sentence with proper question tag:", "She can play the guitar, can't she?", "She can play the guitar, can she?", "She can play the guitar, isn't it?", "She can play the guitar, doesn't she?", 0, "A positive auxiliary 'can' takes the negative tag 'can't she?'."),
            QuestionEntity("c8_eng05", "practice_Class 8_English", "Convert to passive voice: 'The students planted fifty saplings in the school yard.'", "Fifty saplings were planted by the students in the school yard.", "Fifty saplings had been planted by the students.", "Fifty saplings are planted by the students.", "Fifty saplings was planted by the students.", 0, "Simple past passive takes 'were + past participle' for plural objects."),
            QuestionEntity("c8_eng06", "practice_Class 8_English", "In 'The Summit Within', who describes the supreme experience of scaling Mount Everest?", "Major H.P.S. Ahluwalia", "Tenzing Norgay", "Edmund Hillary", "Bachendri Pal", 0, "Major H.P.S. Ahluwalia was part of the 1965 Indian expedition to Everest."),
            QuestionEntity("c8_eng07", "practice_Class 8_English", "What is the synonym of the word 'Ebullient'?", "Cheerful and energetic", "Quiet and reserved", "Angry and frustrated", "Fearful and timid", 0, "Ebullient means overflowing with cheerful enthusiasm and vigor."),
            QuestionEntity("c8_eng08", "practice_Class 8_English", "Choose the correct form of reported speech: The teacher said, 'The sun rises in the east.'", "The teacher said that the sun rises in the east.", "The teacher said that the sun rose in the east.", "The teacher said that the sun had risen in the east.", "The teacher told the sun rises in the east.", 0, "Universal truths and scientific facts do not change tense in indirect speech."),
            QuestionEntity("c8_eng09", "practice_Class 8_English", "Identify the adverb of frequency in the sentence: 'He seldom makes mistakes in his assignments.'", "seldom", "makes", "mistakes", "assignments", 0, "'Seldom' tells how frequently the action occurs (rarely)."),
            QuestionEntity("c8_eng10", "practice_Class 8_English", "In 'Jalebis', what temptation led the schoolboy to spend his school fees money?", "Crisp and fresh jalebis", "Movie tickets", "Cricket bat", "Toy train", 0, "The sweet, syrup-filled jalebis tempted the boy to spend his school fees."),
            QuestionEntity("c8_eng11", "practice_Class 8_English", "Choose the word with the correct spelling:", "Perseverance", "Perseverence", "Persevirence", "Perserverance", 0, "'Perseverance' is the correct spelling, meaning persistence in doing something."),
            QuestionEntity("c8_eng12", "practice_Class 8_English", "What is the meaning of the idiom 'a blessing in disguise'?", "An apparent misfortune that yields unexpected good results", "A magical prayer", "A hidden danger", "A false praise", 0, "A blessing in disguise is something good that seemed bad at first."),
            QuestionEntity("c8_eng13", "practice_Class 8_English", "Fill in the blank with the suitable modal: 'You ___ follow the traffic regulations strictly.'", "must", "might", "could", "may", 0, "'Must' expresses strong obligation and legal compulsion."),
            QuestionEntity("c8_eng14", "practice_Class 8_English", "In the poem 'Geography Lesson', from where did the poet observe the city layout?", "From a jet plane rising into the sky", "From a mountain cliff", "From a tall skyscraper", "From a hot air balloon", 0, "Zulfikar Ghose observed the city and valleys from a jet plane six miles high."),
            QuestionEntity("c8_eng15", "practice_Class 8_English", "Identify the figure of speech: 'The classroom was a bustling beehive.'", "Metaphor", "Simile", "Personification", "Hyperbole", 0, "Comparing the classroom directly to a beehive without using 'like' or 'as' is a metaphor.")
        ),
        // Class 8 SST (CBSE 2026 Our Pasts III, Resources, Civics)
        "Class 8_SST" to listOf(
            QuestionEntity("c8_sst01", "practice_Class 8_SST", "Who was the last powerful Mughal ruler in India?", "Aurangzeb", "Akbar", "Bahadur Shah Zafar", "Shah Jahan", 0, "Aurangzeb was the last powerful Mughal ruler; after his death in 1707, the empire weakened."),
            QuestionEntity("c8_sst02", "practice_Class 8_SST", "The Battle of Plassey was fought in which historic year?", "1757", "1764", "1857", "1761", 0, "In 1757, Robert Clive defeated Nawab Sirajuddaulah at Plassey, beginning British rule in Bengal."),
            QuestionEntity("c8_sst03", "practice_Class 8_SST", "The historic revolt of 1857 started from which military cantonment?", "Meerut", "Jhansi", "Delhi", "Kanpur", 0, "The sepoy mutiny broke out in Meerut on 10th May 1857 before spreading to Delhi."),
            QuestionEntity("c8_sst04", "practice_Class 8_SST", "Which tribal leader led the celebrated Ulgulan (Great Tumult) movement in Chotanagpur?", "Birsa Munda", "Sidhu Murmu", "Kanhu Murmu", "Alluri Sitarama Raju", 0, "Birsa Munda led the Munda rebellion to establish Birsa Raj free of colonial dikus."),
            QuestionEntity("c8_sst05", "practice_Class 8_SST", "Resources that are drawn from nature and used without much modification are called:", "Natural resources", "Human-made resources", "Capital resources", "Renewable resources", 0, "Air, water, soil, and minerals are natural resources drawn directly from nature."),
            QuestionEntity("c8_sst06", "practice_Class 8_SST", "Which method of soil conservation is most effective in steep hill slopes?", "Terrace farming", "Mulching", "Contour ploughing", "Shelter belts", 0, "Terrace farming creates flat steps on hill slopes to reduce surface runoff and prevent soil erosion."),
            QuestionEntity("c8_sst07", "practice_Class 8_SST", "Which Indian city is known as the 'Manchester of India' for its textile industry?", "Ahmedabad", "Mumbai", "Surat", "Coimbatore", 0, "Ahmedabad is situated close to cotton growing tracts with abundant water from the Sabarmati."),
            QuestionEntity("c8_sst08", "practice_Class 8_SST", "The fundamental Right to Education (Article 21A) was added to ensure free schooling for children aged:", "6 to 14 years", "5 to 12 years", "6 to 18 years", "3 to 10 years", 0, "Article 21A guarantees compulsory free education to children between 6 and 14 years."),
            QuestionEntity("c8_sst09", "practice_Class 8_SST", "What is the minimum age prescribed by the Indian Constitution to vote in general elections?", "18 years", "21 years", "25 years", "16 years", 0, "Universal Adult Suffrage grants voting rights to all Indian citizens aged 18 and above."),
            QuestionEntity("c8_sst10", "practice_Class 8_SST", "How many houses comprise the Indian Parliament (Sansad)?", "Two (Lok Sabha and Rajya Sabha)", "Three", "One", "Four", 0, "The Parliament is bicameral, consisting of the Lok Sabha and Rajya Sabha, along with the President."),
            QuestionEntity("c8_sst11", "practice_Class 8_SST", "Who presided over the drafting of the Indian Constitution as Chairman of the Drafting Committee?", "Dr. B.R. Ambedkar", "Dr. Rajendra Prasad", "Jawaharlal Nehru", "Sardar Vallabhbhai Patel", 0, "Dr. B.R. Ambedkar was the chief architect and chairman of the drafting committee."),
            QuestionEntity("c8_sst12", "practice_Class 8_SST", "The separation of religion from state power is fundamentally defined as:", "Secularism", "Democracy", "Socialism", "Federalism", 0, "Secularism guarantees that the government does not promote or discriminate against any religion."),
            QuestionEntity("c8_sst13", "practice_Class 8_SST", "Which is the apex and highest court of appeal in India?", "Supreme Court of India", "High Court", "District Sessions Court", "Lok Adalat", 0, "The Supreme Court in New Delhi is the apex judicial body under the Constitution."),
            QuestionEntity("c8_sst14", "practice_Class 8_SST", "Which agricultural practice is also commonly known as 'Slash and Burn' cultivation?", "Shifting cultivation (Jhum)", "Commercial grain farming", "Plantation agriculture", "Mixed farming", 0, "Shifting cultivation clears forest patches by cutting and burning vegetation before planting crops."),
            QuestionEntity("c8_sst15", "practice_Class 8_SST", "What type of economic activity is agriculture, forestry, and fishing classified under?", "Primary activity", "Secondary activity", "Tertiary activity", "Quaternary activity", 0, "Primary activities involve the direct extraction and production of natural resources.")
        ),
        // Class 8 Hindi (CBSE 2026 Vasant Bhag 3 & Grammar)
        "Class 8_Hindi" to listOf(
            QuestionEntity("c8_hin01", "practice_Class 8_Hindi", "'लाख की चूड़ियाँ' कहानी के मुख्य पात्र 'बदलू' का पेशा क्या था?", "मनिहार (चूड़ियाँ बनाने वाला)", "लोहार", "बढ़ई", "दर्जी", 0, "बदलू एक मनिहार था जो बदल-बदल कर काँच के स्थान पर लाख की सुंदर चूड़ियाँ बनाता था।"),
            QuestionEntity("c8_hin02", "practice_Class 8_Hindi", "'बस की यात्रा' किस विधा की रचना है और इसके लेखक कौन हैं?", "व्यंग्य निबंध - हरिशंकर परसाई", "कहानी - प्रेमचंद", "कविता - रामधारी सिंह 'दिनकर'", "संस्मरण - महादेवी वर्मा", 0, "'बस की यात्रा' प्रसिद्ध व्यंग्यकार हरिशंकर परसाई द्वारा लिखित एक हास्य-व्यंग्य निबंध है।"),
            QuestionEntity("c8_hin03", "practice_Class 8_Hindi", "'दीवानों की हस्ती' कविता के रचयिता कौन हैं?", "भगवती चरण वर्मा", "सूर्यकांत त्रिपाठी 'निराला'", "सुभद्रा कुमारी चौहान", "जयशंकर प्रसाद", 0, "भगवती चरण वर्मा ने 'दीवानों की हस्ती' में स्वाधीनता सेनानियों के उल्लासमय जीवन का वर्णन किया है।"),
            QuestionEntity("c8_hin04", "practice_Class 8_Hindi", "'यह सबसे कठिन समय नहीं' कविता में कवयित्री कौन हैं?", "जया जादवानी", "महादेवी वर्मा", "कृष्णा सोबती", "निर्मला पुतुल", 0, "जया जादवानी ने इस कविता में आशावाद और सकारात्मक दृष्टिकोण को व्यक्त किया है।"),
            QuestionEntity("c8_hin05", "practice_Class 8_Hindi", "'सुदामा चरित' किस कवि द्वारा रचित प्रसिद्ध आख्यान काव्य है?", "नरोत्तमदास", "सूरदास", "तुलसीदास", "रसखान", 0, "नरोत्तमदास जी ने कृष्ण-सुदामा की निश्चल मित्रता पर सवैया छंद में 'सुदामा चरित' रचा।"),
            QuestionEntity("c8_hin06", "practice_Class 8_Hindi", "‘प्रत्येक’ शब्द का सही संधि-विच्छेद क्या होगा?", "प्रति + एक", "प्र + प्रत्येक", "प्रत + एक", "प्रति + एकम्", 0, "प्रति + एक = प्रत्येक (इ + ए = ये, यण् स्वर संधि)।"),
            QuestionEntity("c8_hin07", "practice_Class 8_Hindi", "‘नीलकंठ’ में कौन सा समास है?", "बहुव्रीहि समास", "द्विगु समास", "द्वन्द्व समास", "अव्ययीभाव समास", 0, "नीला है कंठ जिनका अर्थात भगवान शिव (तीसरा अर्थ प्रधान) - बहुव्रीहि समास।"),
            QuestionEntity("c8_hin08", "practice_Class 8_Hindi", "‘अंगूठा दिखाना’ मुहावरे का सही अर्थ क्या है?", "साफ मना करना / इनकार करना", "मदद करना", "चिढ़ाना", "स्वागत करना", 0, "अंगूठा दिखाना अर्थात अंतिम समय पर साफ इनकार कर देना।"),
            QuestionEntity("c8_hin09", "practice_Class 8_Hindi", "जो कम बोलता हो, उसके लिए एक शब्द क्या होगा?", "मितभाषी", "वाचाल", "मूक", "विद्वान", 0, "कम और नपा-तुला बोलने वाले व्यक्ति को मितभाषी कहते हैं।"),
            QuestionEntity("c8_hin10", "practice_Class 8_Hindi", "‘कमल’ का सही पर्यायवाची शब्द कौन सा है?", "सरोज", "वारिद", "पयोधर", "जलद", 0, "सरोज, पंकज, जलज कमल के पर्यायवाची हैं; वारिद बादल का पर्यायवाची है।"),
            QuestionEntity("c8_hin11", "practice_Class 8_Hindi", "शुद्ध वर्तनी वाले शब्द का चयन कीजिए:", "उज्ज्वल", "उज्वल", "उज्जवल", "उजवल", 0, "उज्ज्वल में दोनों 'ज' आधे होते हैं, अतः यह शुद्ध वर्तनी है।"),
            QuestionEntity("c8_hin12", "practice_Class 8_Hindi", "संज्ञा के स्थान पर प्रयोग होने वाले शब्दों को क्या कहते हैं?", "सर्वनाम", "विशेषण", "क्रिया", "अव्यय", 0, "संज्ञा के स्थान पर प्रयोग होने वाले विकारी शब्द सर्वनाम कहलाते हैं।"),
            QuestionEntity("c8_hin13", "practice_Class 8_Hindi", "‘पेड़ से पत्ते गिरते हैं’ - इस वाक्य में कौन सा कारक है?", "अपादान कारक", "करण कारक", "कर्म कारक", "अधिकरण कारक", 0, "किसी वस्तु से अलग होने के अर्थ में अपादान कारक (विभक्ति 'से') होता है।"),
            QuestionEntity("c8_hin14", "practice_Class 8_Hindi", "‘आकाश’ का सही विलोम शब्द क्या है?", "पाताल", "धरती", "समुद्र", "अंबर", 0, "आकाश का विलोम पाताल होता है।"),
            QuestionEntity("c8_hin15", "practice_Class 8_Hindi", "'पानी परात को हाथ छुयो नहिं, नैनन के जल सो पग धोए' - इसमें कौन सा अलंकार है?", "अतिशयोक्ति अलंकार", "उपमा अलंकार", "रूपक अलंकार", "अनुप्रास अलंकार", 0, "बात को अत्यधिक बढ़ा-चढ़ाकर प्रस्तुत करने के कारण यहाँ अतिशयोक्ति अलंकार है।")
        ),
        // Class 10 Mathematics
        "Class 10_Math" to listOf(
            QuestionEntity("m10_01", "practice_Class 10_Math", "Solve for x: 2x + 5 = 15", "x = 5", "x = 10", "x = 2", "x = 15", 0, "Subtract 5 from both sides: 2x = 10, then divide by 2: x = 5."),
            QuestionEntity("m10_02", "practice_Class 10_Math", "What is the area of a circle with radius 7 cm? (Use pi = 22/7)", "154 sq cm", "44 sq cm", "98 sq cm", "14 sq cm", 0, "Area = pi * r^2 = 22/7 * 7 * 7 = 154 sq cm."),
            QuestionEntity("m10_03", "practice_Class 10_Math", "If sin(A) = 3/5, what is cos(A)?", "4/5", "3/4", "5/3", "1", 0, "Using sin^2(A) + cos^2(A) = 1, cos(A) = sqrt(1 - 9/25) = 4/5."),
            QuestionEntity("m10_04", "practice_Class 10_Math", "Find the 10th term of the AP: 2, 7, 12, ...", "47", "50", "42", "37", 0, "a = 2, d = 5. T_10 = a + 9d = 2 + 45 = 47."),
            QuestionEntity("m10_05", "practice_Class 10_Math", "What is the discriminant of the quadratic equation x^2 - 5x + 6 = 0?", "1", "-1", "25", "0", 0, "D = b^2 - 4ac = (-5)^2 - 4(1)(6) = 25 - 24 = 1."),
            QuestionEntity("m10_06", "practice_Class 10_Math", "The sum of all internal angles of a triangle is:", "180 degrees", "360 degrees", "90 degrees", "270 degrees", 0, "The angle sum property of triangles states that internal angles sum to 180 degrees."),
            QuestionEntity("m10_07", "practice_Class 10_Math", "What is the HCF of two consecutive prime numbers?", "1", "2", "0", "None", 0, "Prime numbers have no common factors other than 1, so their HCF is always 1."),
            QuestionEntity("m10_08", "practice_Class 10_Math", "If alpha and beta are the roots of x^2 - 5x + 6 = 0, find alpha + beta.", "5", "-5", "6", "-6", 0, "Sum of roots alpha + beta = -b/a = -(-5)/1 = 5."),
            QuestionEntity("m10_09", "practice_Class 10_Math", "Find the distance of the point (3, 4) from the origin.", "5", "7", "25", "1", 0, "Distance = sqrt(3^2 + 4^2) = sqrt(9 + 16) = sqrt(25) = 5."),
            QuestionEntity("m10_10", "practice_Class 10_Math", "What is the value of tan(45 degrees)?", "1", "0", "1/sqrt(2)", "sqrt(3)", 0, "The trigonometric value of tan(45 degrees) is exactly 1."),
            QuestionEntity("m10_11", "practice_Class 10_Math", "How many tangents can be drawn to a circle from an external point?", "2", "1", "infinite", "0", 0, "From any external point, exactly two tangents can be drawn to a circle."),
            QuestionEntity("m10_12", "practice_Class 10_Math", "What is the empirical relation between mean, median, and mode?", "Mode = 3 Median - 2 Mean", "Mode = 2 Median - 3 Mean", "Median = 3 Mode - 2 Mean", "Mean = 3 Median - 2 Mode", 0, "The standard empirical formula is Mode = 3 Median - 2 Mean."),
            QuestionEntity("m10_13", "practice_Class 10_Math", "What is the probability of a sure/certain event?", "1", "0", "0.5", "infinite", 0, "A certain event has a probability of exactly 1."),
            QuestionEntity("m10_14", "practice_Class 10_Math", "Find the volume of a cylinder of radius r and height h.", "pi * r^2 * h", "2 * pi * r * h", "1/3 * pi * r^2 * h", "4/3 * pi * r^3", 0, "Volume of cylinder is base area times height = pi * r^2 * h."),
            QuestionEntity("m10_15", "practice_Class 10_Math", "If first term of AP is 4 and common difference is 3, find its 5th term.", "16", "13", "19", "12", 0, "a_5 = a + 4d = 4 + 4(3) = 4 + 12 = 16.")
        )
    )

    // Primary school age fallback (Class 1 to 6)
    val primarySubjectQuestions = mapOf(
        "SST" to listOf(
            QuestionEntity("prim_sst1", "practice_generic_SST", "Which city is the capital of India?", "New Delhi", "Mumbai", "Kolkata", "Chennai", 0, "New Delhi is the capital of India."),
            QuestionEntity("prim_sst2", "practice_generic_SST", "How many colors are in the national flag of India?", "3", "4", "2", "5", 0, "The Indian national flag has 3 main bands: saffron, white, and green."),
            QuestionEntity("prim_sst3", "practice_generic_SST", "Which festival is known as the festival of lights?", "Diwali", "Holi", "Eid", "Christmas", 0, "Diwali is celebrated with lamps and lights."),
            QuestionEntity("prim_sst4", "practice_generic_SST", "On which day do we celebrate Independence Day in India?", "15th August", "26th January", "2nd October", "14th November", 0, "India gained independence on 15th August 1947."),
            QuestionEntity("prim_sst5", "practice_generic_SST", "Who is the current President of India?", "Droupadi Murmu", "Narendra Modi", "Ram Nath Kovind", "Pranab Mukherjee", 0, "Smt. Droupadi Murmu is the President of India."),
            QuestionEntity("prim_sst6", "practice_generic_SST", "Which famous monument is located in Agra?", "Taj Mahal", "Red Fort", "Qutub Minar", "Hawa Mahal", 0, "The Taj Mahal is located on the banks of river Yamuna in Agra."),
            QuestionEntity("prim_sst7", "practice_generic_SST", "Which river is considered the longest in the world?", "Nile", "Amazon", "Ganga", "Mississippi", 0, "The Nile River is historically considered the longest river in the world."),
            QuestionEntity("prim_sst8", "practice_generic_SST", "Which planet is known as the Blue Planet?", "Earth", "Mars", "Venus", "Neptune", 0, "Earth is called the blue planet because of its abundant water coverage."),
            QuestionEntity("prim_sst9", "practice_generic_SST", "Who was known as Chhatrapati?", "Shivaji Maharaj", "Maharana Pratap", "Akbar", "Ashoka", 0, "Shivaji Maharaj founded the Maratha Empire and was crowned Chhatrapati."),
            QuestionEntity("prim_sst10", "practice_generic_SST", "Which is the smallest continent by land area?", "Australia", "Europe", "Asia", "Africa", 0, "Australia is the smallest continent on Earth."),
            QuestionEntity("prim_sst11", "practice_generic_SST", "Which ocean is the largest on Earth?", "Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Arctic Ocean", 0, "The Pacific Ocean covers more than 30% of the Earth's surface."),
            QuestionEntity("prim_sst12", "practice_generic_SST", "Who wrote the national anthem of India?", "Rabindranath Tagore", "Bankim Chandra Chattopadhyay", "Mahatma Gandhi", "Sarojini Naidu", 0, "Jana Gana Mana was composed by Rabindranath Tagore."),
            QuestionEntity("prim_sst13", "practice_generic_SST", "Which is the tallest mountain in the world?", "Mount Everest", "K2", "Kangchenjunga", "Lhotse", 0, "Mount Everest is the highest mountain peak above sea level."),
            QuestionEntity("prim_sst14", "practice_generic_SST", "What is the national bird of India?", "Peacock", "Parrot", "Sparrow", "Eagle", 0, "The Indian Peacock (Pavo cristatus) is the national bird of India."),
            QuestionEntity("prim_sst15", "practice_generic_SST", "Which is the largest state in India by area?", "Rajasthan", "Madhya Pradesh", "Maharashtra", "Uttar Pradesh", 0, "Rajasthan is the largest state in India by land area.")
        ),
        "English" to listOf(
            QuestionEntity("prim_eng1", "practice_generic_English", "What is the plural of the word 'cat'?", "cats", "cates", "catse", "cat", 0, "Plural of cat is cats. (Grammar)"),
            QuestionEntity("prim_eng2", "practice_generic_English", "What is the opposite of 'hot'?", "cold", "warm", "wet", "dry", 0, "Cold is the opposite of hot. (Grammar)"),
            QuestionEntity("prim_eng3", "practice_generic_English", "Which letter comes after E?", "F", "D", "G", "H", 0, "The letter F comes after E. (Grammar)"),
            QuestionEntity("prim_eng4", "practice_generic_English", "Identify the action word (verb): 'The boy runs.'", "runs", "boy", "The", "boy runs", 0, "'Runs' is the action word (verb). (Grammar)"),
            QuestionEntity("prim_eng5", "practice_generic_English", "Who wrote the poem about the 'Daffodils'?", "William Wordsworth", "Shakespeare", "Keats", "Milton", 0, "William Wordsworth wrote 'Daffodils'. (Literature)"),
            QuestionEntity("prim_eng6", "practice_generic_English", "What is the correct spelling of the color of grass?", "green", "grene", "grain", "grine", 0, "The correct spelling is green. (Grammar)"),
            QuestionEntity("prim_eng7", "practice_generic_English", "Complete: 'A ___ of sheep was grazing in the valley.'", "flock", "herd", "bunch", "pack", 0, "'Flock' is the correct collective noun for sheep. (Grammar)"),
            QuestionEntity("prim_eng8", "practice_generic_English", "What is a person, place, or thing called?", "Noun", "Verb", "Adjective", "Pronoun", 0, "A noun is a naming word. (Grammar)"),
            QuestionEntity("prim_eng9", "practice_generic_English", "What is the past tense of 'sing'?", "sang", "sung", "singed", "sings", 0, "The past tense of sing is sang. (Grammar)"),
            QuestionEntity("prim_eng10", "practice_generic_English", "Identify the adjective in: 'She has a blue bag.'", "blue", "bag", "has", "bag", 0, "Blue describes the noun bag. (Grammar)"),
            QuestionEntity("prim_eng11", "practice_generic_English", "Who is the main character in the story of 'Alice in Wonderland'?", "Alice", "White Rabbit", "Mad Hatter", "Queen of Hearts", 0, "Alice is the protagonist who falls down the rabbit hole. (Literature)"),
            QuestionEntity("prim_eng12", "practice_generic_English", "What is the synonym of 'happy'?", "joyful", "sad", "angry", "tired", 0, "Joyful has a similar meaning to happy. (Grammar)"),
            QuestionEntity("prim_eng13", "practice_generic_English", "Which word is a pronoun?", "He", "John", "Running", "Beautiful", 0, "'He' replaces a noun. (Grammar)"),
            QuestionEntity("prim_eng14", "practice_generic_English", "What is the antonym of 'huge'?", "tiny", "large", "heavy", "tall", 0, "Tiny means very small, antonym of huge. (Grammar)"),
            QuestionEntity("prim_eng15", "practice_generic_English", "Choose the correct article: 'I saw ___ elephant.'", "an", "a", "the", "no article", 0, "'An' is used before vowel sounds. (Grammar)")
        ),
        "EVS" to listOf(
            QuestionEntity("prim_evs1", "practice_generic_EVS", "Which body organ is used to see things around us?", "Eyes", "Ears", "Nose", "Skin", 0, "We see with our eyes."),
            QuestionEntity("prim_evs2", "practice_generic_EVS", "Which animal is called the King of the Jungle?", "Lion", "Tiger", "Elephant", "Fox", 0, "The Lion is known as the king of the jungle."),
            QuestionEntity("prim_evs3", "practice_generic_EVS", "Plants get water and minerals from the soil through their:", "Roots", "Leaves", "Flowers", "Stem", 0, "Roots absorb water from the soil."),
            QuestionEntity("prim_evs4", "practice_generic_EVS", "Which of the following is a fruit?", "Apple", "Potato", "Onion", "Spinach", 0, "Apple is a fruit, others are vegetables."),
            QuestionEntity("prim_evs5", "practice_generic_EVS", "How many legs does a typical insect have?", "6", "4", "8", "2", 0, "Insects typically have 6 legs."),
            QuestionEntity("prim_evs6", "practice_generic_EVS", "Fish breathe underwater using their:", "Gills", "Lungs", "Skin", "Fins", 0, "Fish use gills to absorb dissolved oxygen from water."),
            QuestionEntity("prim_evs7", "practice_generic_EVS", "What is the main source of water on Earth?", "Rain", "River", "Well", "Sea", 0, "Rain is the primary natural source of freshwater."),
            QuestionEntity("prim_evs8", "practice_generic_EVS", "Which of these is a herbivorous animal?", "Cow", "Tiger", "Lion", "Wolf", 0, "Cows eat only grass and plants."),
            QuestionEntity("prim_evs9", "practice_generic_EVS", "Which part of a plant makes food using sunlight?", "Leaf", "Root", "Stem", "Flower", 0, "Leaves make food through photosynthesis."),
            QuestionEntity("prim_evs10", "practice_generic_EVS", "Which festival marks the harvest season in Punjab?", "Baisakhi", "Pongal", "Onam", "Holi", 0, "Baisakhi is a famous harvest festival in Punjab."),
            QuestionEntity("prim_evs11", "practice_generic_EVS", "Which of these is a biodegradable waste?", "Paper", "Plastic bottle", "Glass jar", "Metal can", 0, "Paper decomposes naturally in the soil."),
            QuestionEntity("prim_evs12", "practice_generic_EVS", "Our national flower is:", "Lotus", "Rose", "Sunflower", "Marigold", 0, "Lotus (Nelumbo nucifera) is our national flower."),
            QuestionEntity("prim_evs13", "practice_generic_EVS", "Which house floats on water?", "Houseboat", "Caravan", "Tent", "Igloo", 0, "Houseboats are houses built on boats in water bodies."),
            QuestionEntity("prim_evs14", "practice_generic_EVS", "What is the boiling point of water?", "100 degrees Celsius", "0 degrees Celsius", "50 degrees Celsius", "200 degrees Celsius", 0, "Water boils at 100 degrees Celsius at sea level."),
            QuestionEntity("prim_evs15", "practice_generic_EVS", "Which sense organ helps us feel hot or cold objects?", "Skin", "Tongue", "Nose", "Eyes", 0, "Our skin contains receptors that detect temperature.")
        ),
        "Hindi" to listOf(
            QuestionEntity("prim_hin1", "practice_generic_Hindi", "भाषा की सबसे छोटी ध्वनि इकाई को क्या कहते हैं?", "वर्ण", "शब्द", "वाक्य", "संज्ञा", 0, "भाषा की सबसे छोटी इकाई वर्ण कहलाती है।"),
            QuestionEntity("prim_hin2", "practice_generic_Hindi", "निम्नलिखित शब्दों में से 'संज्ञा' शब्द पहचानिए:", "किताब", "धीरे", "सुंदर", "खेलना", 0, "किताब एक वस्तु का नाम है, इसलिए यह संज्ञा है।"),
            QuestionEntity("prim_hin3", "practice_generic_Hindi", "दिन का सही विलोम शब्द क्या है?", "रात", "सुबह", "दोपहर", "शाम", 0, "दिन का विलोम शब्द रात होता है।"),
            QuestionEntity("prim_hin4", "practice_generic_Hindi", "सूर्य का पर्यायवाची शब्द कौन सा है?", "सूरज", "चाँद", "तारा", "बादल", 0, "सूरज सूर्य का पर्यायवाची शब्द है।"),
            QuestionEntity("prim_hin5", "practice_generic_Hindi", "पंचतंत्र कहानियों के रचनाकार कौन हैं?", "विष्णु शर्मा", "प्रेमचंद", "तुलसीदास", "कबीर", 0, "पंचतंत्र की कहानियों के रचयिता पंडित विष्णु शर्मा हैं।"),
            QuestionEntity("prim_hin6", "practice_generic_Hindi", "'राम फल खाता है' - इस वाक्य में क्रिया क्या है?", "खाता है", "राम", "फल", "ताजा", 0, "खाता है क्रिया (काम का होना) है।"),
            QuestionEntity("prim_hin7", "practice_generic_Hindi", "स्वर वर्णों की कुल संख्या कितनी होती है?", "11", "33", "25", "52", 0, "हिंदी वर्णमाला में मूल स्वर वर्ण 11 होते हैं।"),
            QuestionEntity("prim_hin8", "practice_generic_Hindi", "निम्नलिखित में से शुद्ध शब्द कौन सा है?", "उज्ज्वल", "उज्वल", "उजवल", "उज्जवल", 0, "उज्ज्वल की वर्तनी शुद्ध है।"),
            QuestionEntity("prim_hin9", "practice_generic_Hindi", "कवि का स्त्रीलिंग रूप क्या होगा?", "कवयित्री", "कविइत्री", "कवयितरी", "कवीत्री", 0, "कवि का सही स्त्रीलिंग रूप कवयित्री है।"),
            QuestionEntity("prim_hin10", "practice_generic_Hindi", "जो सब कुछ जानता हो, उसे क्या कहते हैं?", "सर्वज्ञ", "अल्पज्ञ", "विद्वान", "ज्ञानी", 0, "सब कुछ जानने वाले को सर्वज्ञ कहते हैं।"),
            QuestionEntity("prim_hin11", "practice_generic_Hindi", "आँखों का तारा होना मुहावरे का सही अर्थ क्या है?", "बहुत प्यारा होना", "आँख में दर्द होना", "कम दिखना", "दुश्मन होना", 0, "आँखों का तारा होना अर्थात अत्यधिक प्रिय होना।"),
            QuestionEntity("prim_hin12", "practice_generic_Hindi", "प्रेमचंद द्वारा लिखित प्रसिद्ध कहानी कौन सी है?", "ईदगाह", "पंच परमेश्वर", "दो बैलों की कथा", "उपर्युक्त सभी", 3, "ईदगाह, पंच परमेश्वर और दो बैलों की कथा तीनों मुंशी प्रेमचंद की प्रसिद्ध रचनाएँ हैं।"),
            QuestionEntity("prim_hin13", "practice_generic_Hindi", "लड़का का बहुवचन रूप क्या होगा?", "लड़के", "लड़कों", "लड़काएँ", "लड़केस", 0, "लड़का का सही बहुवचन लड़के होता है।"),
            QuestionEntity("prim_hin14", "practice_generic_Hindi", "हिंदी किस लिपि में लिखी जाती है?", "देवनागरी", "रोमन", "गुरुमुखी", "फारसी", 0, "हिंदी भाषा देवनागरी लिपि में लिखी जाती है।"),
            QuestionEntity("prim_hin15", "practice_generic_Hindi", "'महेश' का सही संधि-विच्छेद क्या है?", "महा + ईश", "मही + ईश", "मह + ईश", "महा + ईस", 0, "महा + ईश = महेश (गुण स्वर संधि)।")
        ),
        "Sanskrit" to listOf(
            QuestionEntity("prim_san1", "practice_generic_Sanskrit", "संस्कृत भाषायाः लिपिः का अस्ति?", "देवनागरी", "रोमन", "गुरुमुखी", "ब्राह्मी", 0, "संस्कृत भाषा देवनागरी लिप्यम् लिख्यते।"),
            QuestionEntity("prim_san2", "practice_generic_Sanskrit", "'पठति' क्रियापदे कः लकारः अस्ति?", "ट् लकारः", "लोट् लकारः", "लङ् लकारः", "विधिलिङ्", 0, "पठति वर्तमानकाले लट् लकारे प्रयुज्यते।"),
            QuestionEntity("prim_san3", "practice_generic_Sanskrit", "'रामः' शब्दस्य प्रथमा बहुवचनम् किम्?", "रामाः", "रामौ", "रामम्", "रामेण", 0, "रामः, रामौ, रामाः - प्रथमा विभक्तिः।"),
            QuestionEntity("prim_san4", "practice_generic_Sanskrit", "संस्कृत वर्णमालायां कति स्वराः सन्ति?", "१३", "११", "२५", "३३", 0, "संस्कृत वर्णमालायां १३ स्वराः मताः।"),
            QuestionEntity("prim_san5", "practice_generic_Sanskrit", "'विद्यालयः' अस्य सन्धि-विच्छेदः कः?", "विद्या + आलयः", "विद्य + आलयः", "विद्या + लयः", "विद् + आलयः", 0, "विद्या + आलयः = विद्यालयः (दीर्घ स्वर सन्धिः)।"),
            QuestionEntity("prim_san6", "practice_generic_Sanskrit", "'बालकः जलं पिबति' - अत्र कर्तृपदम् किम्?", "बालकः", "जलं", "पिबति", "अस्ति", 0, "बालकः क्रियायाः कर्ता अस्ति।"),
            QuestionEntity("prim_san7", "practice_generic_Sanskrit", "संस्कृते कति वचनानि भवन्ति?", "त्रीणि", "द्वे", "एकम्", "चत्वारि", 0, "संस्कृते त्रीणि वचनानि सन्ति - एकवचनम्, द्विवचनम्, बहुवचनम्।"),
            QuestionEntity("prim_san8", "practice_generic_Sanskrit", "'सूर्यः' शब्दस्य पर्यायपदम् किम्?", "भास्करः", "चन्द्रः", "मेघः", "पवनः", 0, "भास्करः सूर्यस्य पर्यायपदम् अस्ति।"),
            QuestionEntity("prim_san9", "practice_generic_Sanskrit", "संस्कृते कति पुरुषाः भवन्ति?", "त्रयः", "द्वौ", "चत्वारः", "पञ्च", 0, "प्रथमपुरुषः, मध्यमपुरुषः, उत्तमपुरुषः - त्रयः पुरुषाः सन्ति।"),
            QuestionEntity("prim_san10", "practice_generic_Sanskrit", "'गच्छति' इति पदस्य मूलधातुः कः?", "गम्", "गच्छ", "गद्", "गै", 0, "गच्छति पदस्य मूलधातुः 'गम्' अस्ति।"),
            QuestionEntity("prim_san11", "practice_generic_Sanskrit", "'सत्यमेव जयते' इति वाक्यं कस्मात् उपनिषदः उद्धृतम्?", "मुण्डकोपनिषत्", "कठोपनिषत्", "ईशावास्योपनिषत्", "केन उपनिषत्", 0, "सत्यमेव जयते मुण्डकोपनिषदः वाक्यम् अस्ति।"),
            QuestionEntity("prim_san12", "practice_generic_Sanskrit", "महाभारतस्य रचयिता कः अस्ति?", "महर्षि वेदव्यासः", "महर्षि वाल्मीकिः", "कालिदासः", "बाणभट्टः", 0, "महाभारतस्य रचयिता महर्षि वेदव्यासः अस्ति।"),
            QuestionEntity("prim_san13", "practice_generic_Sanskrit", "'फलम्' शब्दस्य नपुंसकलिङ्गे प्रथमा द्विवचनम् किम्?", "फले", "फलानि", "फलेन", "फलाय", 0, "फलम्, फले, फलानि - प्रथमा विभक्तिः।"),
            QuestionEntity("prim_san14", "practice_generic_Sanskrit", "अष्टाध्यायी ग्रन्थस्य रचयिता कः?", "पाणिनिः", "पतञ्जलिः", "वरुरुचिः", "भट्टोजिदीक्षितः", 0, "संस्कृत व्याकरणस्य प्रसिद्धग्रन्थस्य अष्टाध्याय्याः रचयिता महर्षिः पाणिनिः अस्ति।"),
            QuestionEntity("prim_san15", "practice_generic_Sanskrit", "'गजः' शब्दस्य अर्थः कः?", "हाथी", "घोड़ा", "शेर", "ऊंट", 0, "गजः इति संस्कृत शब्दस्य अर्थः हाथी (Elephant) अस्ति।")
        ),
        "GK" to listOf(
            QuestionEntity("prim_gk1", "practice_generic_GK", "How many days are there in a normal year?", "365", "366", "360", "300", 0, "A normal year has 365 days."),
            QuestionEntity("prim_gk2", "practice_generic_GK", "Which is the tallest land animal?", "Giraffe", "Elephant", "Camel", "Lion", 0, "The Giraffe is the tallest land animal."),
            QuestionEntity("prim_gk3", "practice_generic_GK", "Which fruit is known as the King of Fruits in India?", "Mango", "Apple", "Banana", "Orange", 0, "Mango is the national fruit and king of fruits in India."),
            QuestionEntity("prim_gk4", "practice_generic_GK", "How many colors are there in a rainbow?", "7", "6", "8", "5", 0, "A rainbow has 7 colors."),
            QuestionEntity("prim_gk5", "practice_generic_GK", "Which shape is perfectly round like a ball?", "Circle", "Square", "Triangle", "Rectangle", 0, "A circle is round."),
            QuestionEntity("prim_gk6", "practice_generic_GK", "How many fingers are there on one human hand?", "5", "10", "4", "6", 0, "A normal human hand has 5 fingers (including the thumb)."),
            QuestionEntity("prim_gk7", "practice_generic_GK", "Who was the first Prime Minister of India?", "Jawaharlal Nehru", "Mahatma Gandhi", "Dr. B.R. Ambedkar", "Subhas Chandra Bose", 0, "Pandit Jawaharlal Nehru was India's first PM."),
            QuestionEntity("prim_gk8", "practice_generic_GK", "Which is the largest country in the world by area?", "Russia", "Canada", "USA", "China", 0, "Russia is the largest country by geographical area."),
            QuestionEntity("prim_gk9", "practice_generic_GK", "Which gas do we breathe in to survive?", "Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen", 0, "Humans need oxygen for respiration."),
            QuestionEntity("prim_gk10", "practice_generic_GK", "Which organ pumps blood in our body?", "Heart", "Lungs", "Brain", "Stomach", 0, "The heart pumps oxygenated blood throughout our body."),
            QuestionEntity("prim_gk11", "practice_generic_GK", "In which state is the Taj Mahal located?", "Uttar Pradesh", "Delhi", "Rajasthan", "Punjab", 0, "The Taj Mahal is in Agra, Uttar Pradesh."),
            QuestionEntity("prim_gk12", "practice_generic_GK", "Which is the national animal of India?", "Tiger", "Lion", "Leopard", "Elephant", 0, "The Royal Bengal Tiger is the national animal of India."),
            QuestionEntity("prim_gk13", "practice_generic_GK", "How many union territories are there in India currently?", "8", "7", "9", "6", 0, "India has 28 states and 8 union territories."),
            QuestionEntity("prim_gk14", "practice_generic_GK", "What is the capital of France?", "Paris", "London", "Rome", "Berlin", 0, "Paris is the capital and largest city of France."),
            QuestionEntity("prim_gk15", "practice_generic_GK", "Which festival is celebrated to mark the birth of Lord Krishna?", "Janmashtami", "Ram Navami", "Ganesh Chaturthi", "Diwali", 0, "Janmashtami celebrates the birth of Lord Krishna.")
        ),
        "Computer" to listOf(
            QuestionEntity("prim_comp1", "practice_generic_Computer", "Which part of a computer is used to type letters?", "Keyboard", "Mouse", "Monitor", "Printer", 0, "We type using a keyboard."),
            QuestionEntity("prim_comp2", "practice_generic_Computer", "Which part of a computer looks like a television screen?", "Monitor", "CPU", "Keyboard", "Speaker", 0, "The monitor displays output and looks like a TV."),
            QuestionEntity("prim_comp3", "practice_generic_Computer", "Which small computer device is named after an animal?", "Mouse", "Cat", "Dog", "Lion", 0, "The pointing device is called a mouse."),
            QuestionEntity("prim_comp4", "practice_generic_Computer", "What is called the brain of a computer?", "CPU", "UPS", "Monitor", "Keyboard", 0, "CPU is the Central Processing Unit, the brain of the computer."),
            QuestionEntity("prim_comp5", "practice_generic_Computer", "Which key on a keyboard is used to make blank spaces between words?", "Spacebar", "Enter key", "Backspace", "Shift key", 0, "The Spacebar adds spaces."),
            QuestionEntity("prim_comp6", "practice_generic_Computer", "We print our computer drawings on paper using a:", "Printer", "Monitor", "Scanner", "Mouse", 0, "A printer puts drawings and text onto paper."),
            QuestionEntity("prim_comp7", "practice_generic_Computer", "What does RAM stand for?", "Random Access Memory", "Read All Memory", "Run Active Memory", "Ready Action Memory", 0, "RAM stands for Random Access Memory, which is temporary storage."),
            QuestionEntity("prim_comp8", "practice_generic_Computer", "Which of the following is an storage device?", "Hard Disk", "Keyboard", "Monitor", "Printer", 0, "Hard Disk stores files and data permanently."),
            QuestionEntity("prim_comp9", "practice_generic_Computer", "What is the main screen of Windows OS called?", "Desktop", "Home page", "Dashboard", "File manager", 0, "The main working area after booting is the Desktop."),
            QuestionEntity("prim_comp10", "practice_generic_Computer", "Which key is used to delete characters to the left of the cursor?", "Backspace", "Delete key", "Enter", "Spacebar", 0, "Backspace deletes text backwards (left)."),
            QuestionEntity("prim_comp11", "practice_generic_Computer", "Which software is used to surf the internet?", "Web Browser", "MS Paint", "Notepad", "Calculator", 0, "A web browser (like Chrome) is used to load web pages."),
            QuestionEntity("prim_comp12", "practice_generic_Computer", "What is the full form of WWW?", "World Wide Web", "World Wide Word", "Wide Web World", "World Web Wide", 0, "WWW stands for World Wide Web."),
            QuestionEntity("prim_comp13", "practice_generic_Computer", "Which of the following is an input device?", "Scanner", "Printer", "Monitor", "Speaker", 0, "A scanner takes physical sheets and inputs them digitally."),
            QuestionEntity("prim_comp14", "practice_generic_Computer", "What type of device are Speakers?", "Output", "Input", "Storage", "Processing", 0, "Speakers output sound, making them output devices."),
            QuestionEntity("prim_comp15", "practice_generic_Computer", "What is the shortcut key to copy selected items?", "Ctrl + C", "Ctrl + V", "Ctrl + X", "Ctrl + Z", 0, "Ctrl+C is the copy shortcut.")
        ),
        "Math" to listOf(
            QuestionEntity("prim_m1", "practice_generic_Math", "What is 5 plus 7?", "12", "11", "10", "13", 0, "5 + 7 = 12."),
            QuestionEntity("prim_m2", "practice_generic_Math", "How many corners does a square have?", "4", "3", "5", "6", 0, "A square has 4 corners and 4 equal sides."),
            QuestionEntity("prim_m3", "practice_generic_Math", "What number comes just before 50?", "49", "51", "48", "40", 0, "49 is the predecessor of 50."),
            QuestionEntity("prim_m4", "practice_generic_Math", "What is double of 15?", "30", "20", "25", "40", 0, "Double of 15 is 15 * 2 = 30."),
            QuestionEntity("prim_m5", "practice_generic_Math", "Subtract 9 from 20:", "11", "12", "10", "9", 0, "20 - 9 = 11."),
            QuestionEntity("prim_m6", "practice_generic_Math", "How many minutes are in 1 hour?", "60 minutes", "50 minutes", "100 minutes", "120 minutes", 0, "There are exactly 60 minutes in an hour."),
            QuestionEntity("prim_m7", "practice_generic_Math", "Solve: 8 multiplied by 7.", "56", "48", "64", "54", 0, "8 * 7 = 56."),
            QuestionEntity("prim_m8", "practice_generic_Math", "What is the value of 100 divided by 4?", "25", "20", "50", "30", 0, "100 / 4 = 25."),
            QuestionEntity("prim_m9", "practice_generic_Math", "How many days are in a leap year?", "366 days", "365 days", "364 days", "300 days", 0, "Leap years have 366 days due to an extra day in February."),
            QuestionEntity("prim_m10", "practice_generic_Math", "What is the value of 3^3 (3 cubed)?", "27", "9", "18", "12", 0, "3 * 3 * 3 = 27."),
            QuestionEntity("prim_m11", "practice_generic_Math", "Which of the following is an even number?", "24", "13", "37", "49", 0, "Even numbers end in 0, 2, 4, 6, or 8."),
            QuestionEntity("prim_m12", "practice_generic_Math", "What fraction represents a half?", "1/2", "1/4", "3/4", "1/3", 0, "A half is written as 1/2."),
            QuestionEntity("prim_m13", "practice_generic_Math", "Add: 125 + 75.", "200", "150", "250", "225", 0, "125 + 75 = 200."),
            QuestionEntity("prim_m14", "practice_generic_Math", "A triangle with two equal sides is called:", "Isosceles", "Equilateral", "Scalene", "Right-angled", 0, "Isosceles triangles have exactly 2 equal sides."),
            QuestionEntity("prim_m15", "practice_generic_Math", "What is 450 - 150?", "300", "200", "250", "350", 0, "450 - 150 = 300.")
        ),
        "Science" to listOf(
            QuestionEntity("prim_sci1", "practice_generic_Science", "What gas do plants release during photosynthesis?", "Oxygen", "Carbon Dioxide", "Nitrogen", "Hydrogen", 0, "Plants produce oxygen for humans and animals to breathe."),
            QuestionEntity("prim_sci2", "practice_generic_Science", "What is the solid state of water called?", "Ice", "Steam", "Rain", "Snow", 0, "Ice is water in solid form."),
            QuestionEntity("prim_sci3", "practice_generic_Science", "Which planet is closest to the Sun?", "Mercury", "Venus", "Earth", "Mars", 0, "Mercury is the first planet in the solar system."),
            QuestionEntity("prim_sci4", "practice_generic_Science", "What is the main source of light and heat for Earth?", "Sun", "Moon", "Stars", "Fire", 0, "The Sun provides energy to sustain life on Earth."),
            QuestionEntity("prim_sci5", "practice_generic_Science", "Gravity is a force that:", "Pulls things down", "Pushes things away", "Heats things up", "Makes things float", 0, "Gravity pulls objects toward the center of the Earth."),
            QuestionEntity("prim_sci6", "practice_generic_Science", "Which part of a plant makes food?", "Leaf", "Root", "Flower", "Stem", 0, "Leaves make food through photosynthesis using chlorophyll."),
            QuestionEntity("prim_sci7", "practice_generic_Science", "Water boils at what temperature?", "100 degrees Celsius", "0 degrees Celsius", "50 degrees Celsius", "120 degrees Celsius", 0, "Standard boiling point of water is 100 degrees C."),
            QuestionEntity("prim_sci8", "practice_generic_Science", "What makes plant leaves green?", "Chlorophyll", "Photosynthesis", "Oxygen", "Water", 0, "Chlorophyll pigment absorbs solar energy and colors leaves green."),
            QuestionEntity("prim_sci9", "practice_generic_Science", "Which of the following is a non-metal?", "Carbon", "Iron", "Copper", "Gold", 0, "Carbon is a non-metal; the others are metals."),
            QuestionEntity("prim_sci10", "practice_generic_Science", "Which organ is used to breathe in humans?", "Lungs", "Heart", "Kidneys", "Brain", 0, "Lungs exchange gases in human respiration."),
            QuestionEntity("prim_sci11", "practice_generic_Science", "How many bones are in an adult human skeleton?", "206", "300", "208", "180", 0, "An adult human has 206 bones; infants have more."),
            QuestionEntity("prim_sci12", "practice_generic_Science", "What state of matter has a fixed volume but not a fixed shape?", "Liquid", "Solid", "Gas", "Plasma", 0, "Liquids take the shape of their container but have fixed volume."),
            QuestionEntity("prim_sci13", "practice_generic_Science", "Which vitamin is obtained from exposure to sunlight?", "Vitamin D", "Vitamin A", "Vitamin C", "Vitamin B12", 0, "Sunlight triggers synthesis of Vitamin D in human skin."),
            QuestionEntity("prim_sci14", "practice_generic_Science", "Which instrument measures temperature?", "Thermometer", "Barometer", "Speedometer", "Lactometer", 0, "A thermometer measures heat/temperature."),
            QuestionEntity("prim_sci15", "practice_generic_Science", "Sound cannot travel through which of the following?", "Vacuum", "Air", "Water", "Steel", 0, "Sound waves need a medium and cannot travel in a vacuum.")
        ),
        "Mental Math" to listOf(
            QuestionEntity("prim_mm1", "practice_generic_Mental_Math", "What is the product of 12 and 8?", "96", "84", "108", "92", 0, "12 * 8 = 96. (Mental Arithmetic)"),
            QuestionEntity("prim_mm2", "practice_generic_Mental_Math", "What is half of 250?", "125", "120", "130", "150", 0, "250 / 2 = 125. (Mental Arithmetic)"),
            QuestionEntity("prim_mm3", "practice_generic_Mental_Math", "Find the value of (45 + 55) - 30:", "70", "80", "60", "90", 0, "(45 + 55) = 100, then 100 - 30 = 70."),
            QuestionEntity("prim_mm4", "practice_generic_Mental_Math", "How many 50 paise coins make 10 rupees?", "20", "10", "15", "25", 0, "1 rupee = 2 fifty paise coins. So 10 rupees = 10 * 2 = 20 coins."),
            QuestionEntity("prim_mm5", "practice_generic_Mental_Math", "What is 15 multiplied by 4 plus 20?", "80", "75", "85", "90", 0, "(15 * 4) + 20 = 60 + 20 = 80."),
            QuestionEntity("prim_mm6", "practice_generic_Mental_Math", "What is the place value of 7 in 4,785?", "700", "70", "7000", "7", 0, "In 4,785, 7 is in the hundreds place, so its value is 700."),
            QuestionEntity("prim_mm7", "practice_generic_Mental_Math", "If a clock shows 3:45, how many minutes are left until 4:00?", "15 minutes", "20 minutes", "10 minutes", "25 minutes", 0, "60 - 45 = 15 minutes."),
            QuestionEntity("prim_mm8", "practice_generic_Mental_Math", "Find the missing number in the sequence: 5, 10, 15, 20, __?", "25", "30", "22", "24", 0, "The sequence increases by 5 each time: 20 + 5 = 25."),
            QuestionEntity("prim_mm9", "practice_generic_Mental_Math", "What is the square of 9?", "81", "72", "99", "18", 0, "9 * 9 = 81."),
            QuestionEntity("prim_mm10", "practice_generic_Mental_Math", "How many hours are there in 3 full days?", "72 hours", "48 hours", "24 hours", "96 hours", 0, "3 * 24 = 72 hours."),
            QuestionEntity("prim_mm11", "practice_generic_Mental_Math", "Solve quickly: 500 - 175 = ?", "325", "335", "315", "425", 0, "500 - 175 = 325."),
            QuestionEntity("prim_mm12", "practice_generic_Mental_Math", "Which number added to 68 gives exactly 100?", "32", "42", "22", "38", 0, "100 - 68 = 32."),
            QuestionEntity("prim_mm13", "practice_generic_Mental_Math", "What is 1/4th (one fourth) of 100?", "25", "20", "50", "10", 0, "100 / 4 = 25."),
            QuestionEntity("prim_mm14", "practice_generic_Mental_Math", "The Roman numeral for 40 is:", "XL", "LX", "XXXX", "IV", 0, "In Roman numerals, 40 is represented as XL (50 - 10)."),
            QuestionEntity("prim_mm15", "practice_generic_Mental_Math", "If one pen costs Rs. 8, what is the cost of 7 pens?", "Rs. 56", "Rs. 48", "Rs. 64", "Rs. 54", 0, "7 * 8 = Rs. 56.")
        )
    )

    // Middle/secondary school age fallback (Class 6 to 9)
    val secondarySubjectQuestions = mapOf(
        "EVS" to listOf(
            QuestionEntity("sec_evs1", "practice_generic_EVS", "Which layer of the atmosphere contains the protective Ozone Layer?", "Stratosphere", "Troposphere", "Mesosphere", "Thermosphere", 0, "The ozone layer is mainly found in the lower portion of the stratosphere (15-35 km)."),
            QuestionEntity("sec_evs2", "practice_generic_EVS", "World Environment Day is celebrated every year on:", "5th June", "22nd April", "16th September", "1st December", 0, "UN World Environment Day is observed on June 5 across the globe."),
            QuestionEntity("sec_evs3", "practice_generic_EVS", "The historic Chipko Movement in Uttarakhand was associated with the protection of:", "Forests and Trees", "Rivers and Lakes", "Wildlife Sanctuary", "Agricultural Soil", 0, "The Chipko movement began in 1973 to prevent commercial logging of forest trees by hugging them."),
            QuestionEntity("sec_evs4", "practice_generic_EVS", "Which of the following is an exhaustible / non-renewable source of energy?", "Coal", "Solar Energy", "Wind Energy", "Biomass", 0, "Coal is a fossil fuel formed over millions of years and cannot be replenished quickly."),
            QuestionEntity("sec_evs5", "practice_generic_EVS", "The process of excessive nutrient enrichment in water bodies leading to algal bloom is:", "Eutrophication", "Biomagnification", "Salinization", "Acidification", 0, "Eutrophication leads to algal blooms and depletion of dissolved oxygen in aquatic ecosystems."),
            QuestionEntity("sec_evs6", "practice_generic_EVS", "Which gases are primarily responsible for the formation of Acid Rain?", "Sulphur dioxide (SO2) and Nitrogen oxides (NOx)", "Carbon dioxide", "Oxygen and Nitrogen", "Methane", 0, "SO2 and NOx react with atmospheric water vapor to form sulfuric and nitric acids."),
            QuestionEntity("sec_evs7", "practice_generic_EVS", "Kaziranga National Park in Assam is famous for protecting which endangered species?", "Great Indian One-horned Rhinoceros", "Royal Bengal Tiger", "Asiatic Lion", "Snow Leopard", 0, "Kaziranga hosts two-thirds of the world's great one-horned rhinoceroses."),
            QuestionEntity("sec_evs8", "practice_generic_EVS", "What does the '3 R' environmental waste management mantra stand for?", "Reduce, Reuse, Recycle", "Read, Record, Repeat", "Remove, Replace, Return", "Recharge, Rebuild, Revise", 0, "Reduce consumption, Reuse materials, and Recycle waste."),
            QuestionEntity("sec_evs9", "practice_generic_EVS", "Which trophic level in an ecological food chain contains the maximum available energy?", "Producers (Green Plants)", "Primary Consumers (Herbivores)", "Secondary Consumers", "Apex Predators", 0, "According to Lindeman's 10% law, primary producers capture and retain the highest energy."),
            QuestionEntity("sec_evs10", "practice_generic_EVS", "Which international treaty aimed at phasing out substances that deplete the ozone layer?", "Montreal Protocol", "Kyoto Protocol", "Paris Agreement", "Ramsar Convention", 0, "The 1987 Montreal Protocol specifically targeted the phaseout of chlorofluorocarbons (CFCs)."),
            QuestionEntity("sec_evs11", "practice_generic_EVS", "What is the leading driver of deforestation across tropical regions?", "Agricultural expansion and Cattle farming", "Mining", "Urban construction", "Forest fires", 0, "Expansion of farmland and cattle grazing accounts for the largest portion of tropical forest loss."),
            QuestionEntity("sec_evs12", "practice_generic_EVS", "Which physical unit is universally used to measure noise pollution loudness?", "Decibel (dB)", "Hertz (Hz)", "Pascal (Pa)", "Joule (J)", 0, "Noise loudness and acoustic sound pressure levels are measured in decibels (dB)."),
            QuestionEntity("sec_evs13", "practice_generic_EVS", "Minamata disease in Japan was caused by chronic environmental poisoning from:", "Methylmercury", "Lead", "Cadmium", "Arsenic", 0, "Industrial discharge of methylmercury into Minamata Bay bioaccumulated in marine fish."),
            QuestionEntity("sec_evs14", "practice_generic_EVS", "The Sundarbans mangrove forest delta is formed at the confluence of which rivers?", "Ganga and Brahmaputra", "Indus and Yamuna", "Narmada and Tapti", "Godavari and Krishna", 0, "Sundarbans is the world's largest mangrove delta formed by the Ganga and Brahmaputra rivers."),
            QuestionEntity("sec_evs15", "practice_generic_EVS", "Which bio-organism is widely regarded as a sensitive natural bioindicator of clean air?", "Lichens", "Earthworms", "Frogs", "Houseflies", 0, "Lichens are extremely sensitive to sulphur dioxide pollution and vanish in contaminated air.")
        ),
        "Hindi Grammar" to listOf(
            QuestionEntity("sec_hg1", "practice_generic_Hindi_Grammar", "‘परोपकार’ शब्द में कौन सी संधि है?", "गुण स्वर संधि", "दीर्घ स्वर संधि", "वृद्धि स्वर संधि", "यण स्वर संधि", 0, "पर + उपकार = परोपकार (अ + उ = ओ, गुण स्वर संधि)।"),
            QuestionEntity("sec_hg2", "practice_generic_Hindi_Grammar", "‘चौराहा’ शब्द में कौन सा समास है?", "द्विगु समास", "द्वन्द्व समास", "तत्पुरुष समास", "कर्मधारय समास", 0, "जिस समास का प्रथम पद संख्यावाचक हो (चार राहों का समूह), वह द्विगु समास कहलाता है।"),
            QuestionEntity("sec_hg3", "practice_generic_Hindi_Grammar", "‘अभिमान’ शब्द में कौन सा उपसर्ग जुड़ा है?", "अभि", "अप", "अति", "अनु", 0, "अभि + मान = अभिमान, इसमें 'अभि' उपसर्ग लगा है।"),
            QuestionEntity("sec_hg4", "practice_generic_Hindi_Grammar", "‘सफलता’ शब्द में प्रयुक्त प्रत्यय कौन सा है?", "ता", "ल", "ता", "आ", 0, "सफल + ता = सफलता, यहाँ भाववाचक प्रत्यय 'ता' है।"),
            QuestionEntity("sec_hg5", "practice_generic_Hindi_Grammar", "व्याकरण के अनुसार एक सार्थक वाक्य के कितने मुख्य घटक होते हैं?", "दो (उद्देश्य और विधेय)", "तीन", "चार", "पाँच", 0, "प्रत्येक सार्थक वाक्य के दो आवश्यक अंग होते हैं: उद्देश्य (Subject) और विधेय (Predicate)।"),
            QuestionEntity("sec_hg6", "practice_generic_Hindi_Grammar", "‘हाथ-पैर मारना’ मुहावरे का सही अर्थ क्या है?", "कठिन प्रयास / कोशिश करना", "लड़ाई करना", "नदी में तैरना", "घबरा जाना", 0, "हाथ-पैर मारना अर्थात किसी कार्य में निरंतर प्रयत्न करना।"),
            QuestionEntity("sec_hg7", "practice_generic_Hindi_Grammar", "वीर रस का स्थायी भाव क्या होता है?", "उत्साह", "क्रोध", "शोक", "विस्मय", 0, "वीर रस का स्थायी भाव उत्साह है।"),
            QuestionEntity("sec_hg8", "practice_generic_Hindi_Grammar", "‘चरण कमल बंदौ हरिराई’ में कौन सा अलंकार प्रयुक्त हुआ है?", "रूपक अलंकार", "उपमा अलंकार", "उत्प्रेक्षा अलंकार", "अनुप्रास अलंकार", 0, "चरणों में कमल का अभेद आरोप होने के कारण यहाँ रूपक अलंकार है।"),
            QuestionEntity("sec_hg9", "practice_generic_Hindi_Grammar", "रचना के आधार पर वाक्य के कितने भेद होते हैं?", "तीन (सरल, संयुक्त, मिश्र)", "दो", "चार", "आठ", 0, "रचना के आधार पर वाक्य तीन प्रकार के होते हैं: सरल वाक्य, संयुक्त वाक्य और मिश्र वाक्य।"),
            QuestionEntity("sec_hg10", "practice_generic_Hindi_Grammar", "‘पेड़ से पत्ता गिरा’ - इस वाक्य में कौन सा कारक है?", "अपादान कारक", "करण कारक", "कर्म कारक", "संबोधन कारक", 0, "अलगाव (अलग होने) के भाव में अपादान कारक (विभक्ति चिह्न 'से') होता है।"),
            QuestionEntity("sec_hg11", "practice_generic_Hindi_Grammar", "निम्नलिखित में से शुद्ध वर्तनी वाला शब्द चुनिए:", "आशीर्वाद", "आशिर्वाद", "आशीर्बाद", "अशीर्वाद", 0, "‘आशीर्वाद’ शब्द की वर्तनी पूर्णतः शुद्ध है।"),
            QuestionEntity("sec_hg12", "practice_generic_Hindi_Grammar", "‘अमृत’ का सही विलोम शब्द क्या है?", "विष", "गरल", "जल", "अग्नि", 0, "अमृत का विलोम शब्द विष (या गरल) होता है।"),
            QuestionEntity("sec_hg13", "practice_generic_Hindi_Grammar", "‘कमल’ का पर्यायवाची शब्द कौन सा नहीं है?", "वारिद", "पंकज", "जलज", "सरोज", 0, "वारिद बादल का पर्यायवाची है, जबकि पंकज, जलज, सरोज कमल के पर्यायवाची हैं।"),
            QuestionEntity("sec_hg14", "practice_generic_Hindi_Grammar", "संज्ञा के स्थान पर प्रयुक्त होने वाले शब्दों को क्या कहते हैं?", "सर्वनाम", "विशेषण", "क्रिया", "अव्यय", 0, "संज्ञा के स्थान पर प्रयोग किए जाने वाले शब्द सर्वनाम कहलाते हैं।"),
            QuestionEntity("sec_hg15", "practice_generic_Hindi_Grammar", "‘प्रत्यक्ष’ का सही संधि-विच्छेद क्या है?", "प्रति + अक्ष", "प्र + त्यक्ष", "प्रत्य + क्ष", "प्रति + यक्ष", 0, "प्रति + अक्ष = प्रत्यक्ष (इ + अ = य, यण् स्वर संधि)।")
        ),
        "English Grammar" to listOf(
            QuestionEntity("sec_eg1", "practice_generic_English_Grammar", "Choose the correct sentence with proper Subject-Verb Agreement:", "Neither the teacher nor the students were absent.", "Neither the teacher nor the students was absent.", "Each of the players have scored.", "Every boy and girl were excited.", 0, "When subjects are joined by 'neither...nor', the verb agrees with the closer subject ('students were')."),
            QuestionEntity("sec_eg2", "practice_generic_English_Grammar", "Identify the passive voice of: 'Someone stole my bicycle yesterday.'", "My bicycle was stolen yesterday.", "My bicycle is stolen yesterday.", "My bicycle has been stolen.", "Someone was stealing my bicycle.", 0, "Simple past active ('stole') converts to 'was stolen' in passive voice."),
            QuestionEntity("sec_eg3", "practice_generic_English_Grammar", "Convert to Indirect Speech: He said, 'I am working hard.'", "He said that he was working hard.", "He said that I am working hard.", "He says that he is working hard.", "He told he had worked hard.", 0, "Present continuous changes to past continuous: 'am working' -> 'was working'."),
            QuestionEntity("sec_eg4", "practice_generic_English_Grammar", "Fill in the blank with appropriate preposition: 'She is proficient ___ mathematics.'", "in", "at", "with", "on", 0, "The standard grammatical idiom is 'proficient in' a subject or skill."),
            QuestionEntity("sec_eg5", "practice_generic_English_Grammar", "Identify the conditional type: 'If you work diligently, you will succeed.'", "First Conditional", "Zero Conditional", "Second Conditional", "Third Conditional", 0, "If + present simple, will + base verb expresses a real possibility (First Conditional)."),
            QuestionEntity("sec_eg6", "practice_generic_English_Grammar", "What is the antonym of the word 'Meticulous'?", "Careless", "Thorough", "Detailed", "Strict", 0, "Meticulous means showing great care and attention to detail; opposite is Careless."),
            QuestionEntity("sec_eg7", "practice_generic_English_Grammar", "Choose the correctly spelled word:", "Accommodate", "Acommodate", "Accomodate", "Acomodate", 0, "Accommodate contains double 'c' and double 'm'."),
            QuestionEntity("sec_eg8", "practice_generic_English_Grammar", "Complete the idiom: 'To beat around the ___'", "bush", "tree", "forest", "grass", 0, "'Beat around the bush' means speaking evasively and avoiding the main point."),
            QuestionEntity("sec_eg9", "practice_generic_English_Grammar", "Which part of speech is the word 'fast' in: 'He ran fast.'?", "Adverb", "Adjective", "Noun", "Verb", 0, "'Fast' modifies the verb 'ran', indicating how he ran (Adverb)."),
            QuestionEntity("sec_eg10", "practice_generic_English_Grammar", "Choose the correct article: 'Honesty is ___ best policy.'", "the", "a", "an", "no article", 0, "Superlative adjectives ('best') take the definite article 'the'."),
            QuestionEntity("sec_eg11", "practice_generic_English_Grammar", "What is the past participle form of 'fly'?", "flown", "flew", "flyed", "flying", 0, "fly (present) -> flew (past simple) -> flown (past participle)."),
            QuestionEntity("sec_eg12", "practice_generic_English_Grammar", "Identify the noun clause: 'I believe that she will win the competition.'", "that she will win the competition", "I believe", "she will win", "I believe that", 0, "'That she will win...' functions as the direct object of the verb 'believe'."),
            QuestionEntity("sec_eg13", "practice_generic_English_Grammar", "Select the modal verb expressing polite request:", "Could", "Must", "Should", "Shall", 0, "'Could you please lend me your notebook?' is a standard polite request modal."),
            QuestionEntity("sec_eg14", "practice_generic_English_Grammar", "Find the synonym of 'Tenacious':", "Persistent", "Fragile", "Timid", "Weak", 0, "Tenacious means firm, persistent, and determined in holding on."),
            QuestionEntity("sec_eg15", "practice_generic_English_Grammar", "Choose the correct question tag: 'You are coming tomorrow, ___?'", "aren't you", "are you", "won't you", "isn't it", 0, "A positive statement with 'are' takes the negative tag 'aren't you?'.")
        ),
        "Hindi Grammer" to listOf(
            QuestionEntity("sec_hg1_alt", "practice_generic_Hindi_Grammar", "‘परोपकार’ शब्द में कौन सी संधि है?", "गुण स्वर संधि", "दीर्घ स्वर संधि", "वृद्धि स्वर संधि", "यण स्वर संधि", 0, "पर + उपकार = परोपकार (गुण स्वर संधि)।"),
            QuestionEntity("sec_hg2_alt", "practice_generic_Hindi_Grammar", "‘चौराहा’ शब्द में कौन सा समास है?", "द्विगु समास", "द्वन्द्व समास", "तत्पुरुष समास", "कर्मधारय समास", 0, "चार राहों का समूह = द्विगु समास।"),
            QuestionEntity("sec_hg3_alt", "practice_generic_Hindi_Grammar", "‘अभिमान’ शब्द में कौन सा उपसर्ग जुड़ा है?", "अभि", "अप", "अति", "अनु", 0, "अभि + मान = अभिमान ('अभि' उपसर्ग)।"),
            QuestionEntity("sec_hg4_alt", "practice_generic_Hindi_Grammar", "‘सफलता’ शब्द में प्रयुक्त प्रत्यय कौन सा है?", "ता", "ल", "ता", "आ", 0, "सफल + ता = सफलता ('ता' प्रत्यय)।"),
            QuestionEntity("sec_hg5_alt", "practice_generic_Hindi_Grammar", "वाक्य के दो मुख्य घटक कौन से होते हैं?", "उद्देश्य और विधेय", "संज्ञा और क्रिया", "शब्द और वर्ण", "कर्ता और कर्म", 0, "उद्देश्य और विधेय वाक्य के दो मुख्य अनिवार्य अंग हैं।")
        ),
        "English Grammer" to listOf(
            QuestionEntity("sec_eg1_alt", "practice_generic_English_Grammar", "Choose the correct sentence with proper Subject-Verb Agreement:", "Neither the teacher nor the students were absent.", "Neither the teacher nor the students was absent.", "Each of the players have scored.", "Every boy and girl were excited.", 0, "Verb agrees with closest plural subject: 'students were'."),
            QuestionEntity("sec_eg2_alt", "practice_generic_English_Grammar", "Identify the passive voice of: 'Someone stole my bicycle yesterday.'", "My bicycle was stolen yesterday.", "My bicycle is stolen yesterday.", "My bicycle has been stolen.", "Someone was stealing my bicycle.", 0, "Past passive: 'was stolen'."),
            QuestionEntity("sec_eg3_alt", "practice_generic_English_Grammar", "Convert to Indirect Speech: He said, 'I am working hard.'", "He said that he was working hard.", "He said that I am working hard.", "He says that he is working hard.", "He told he had worked hard.", 0, "'am working' becomes 'was working'."),
            QuestionEntity("sec_eg4_alt", "practice_generic_English_Grammar", "Fill in the blank: 'She is proficient ___ mathematics.'", "in", "at", "with", "on", 0, "Idiomatic preposition is 'proficient in'."),
            QuestionEntity("sec_eg5_alt", "practice_generic_English_Grammar", "What is the antonym of the word 'Meticulous'?", "Careless", "Thorough", "Detailed", "Strict", 0, "Meticulous means careful; antonym is Careless.")
        ),
        "Hindi" to listOf(
            QuestionEntity("sec_hn1", "practice_generic_Hindi", "सज्जन का सही संधि-विच्छेद क्या होगा?", "सत् + जन", "सज् + जन", "सज + जन", "सत + जन", 0, "सत् + जन = सज्जन (व्यंजन संधि)। (Grammar)"),
            QuestionEntity("sec_hn2", "practice_generic_Hindi", "सूर्यकांत त्रिपाठी 'निराला' की प्रसिद्ध कविता कौन सी है?", "ध्वनि", "कबीर की साखियाँ", "लाख की चूड़ियाँ", "सुदामा चरित", 0, "'ध्वनि' कविता सूर्यकांत त्रिपाठी 'निराला' द्वारा रचित है। (Literature)"),
            QuestionEntity("sec_hn3", "practice_generic_Hindi", "शुद्ध वर्तनी वाले शब्द का चयन कीजिए:", "कवयित्री", "कविइत्री", "कवयितरी", "कवीत्री", 0, "कवयित्री शुद्ध वर्तनी रूप है। (Grammar)"),
            QuestionEntity("sec_hn4", "practice_generic_Hindi", "प्रेमचंद का वास्तविक नाम क्या था?", "धनपत राय", "नवाब राय", "गणपत राय", "रामचंद्र", 0, "मुंशी प्रेमचंद का वास्तविक नाम धनपत राय श्रीवास्तव था। (Literature)"),
            QuestionEntity("sec_hn5", "practice_generic_Hindi", "जो सब कुछ जानता हो, उसके लिए एक शब्द:", "सर्वज्ञ", "अल्पज्ञ", "बुद्धिमान", "विद्वान", 0, "सब कुछ जानने वाले को सर्वज्ञ कहते हैं। (Grammar)"),
            QuestionEntity("sec_hn6", "practice_generic_Hindi", "करुण रस का स्थायी भाव क्या होता है?", "शोक", "भय", "क्रोध", "उत्साह", 0, "करुण रस का स्थायी भाव शोक है। (Grammar)"),
            QuestionEntity("sec_hn7", "practice_generic_Hindi", "लंबोदर में कौन सा समास है?", "बहुव्रीहि समास", "द्विगु समास", "तत्पुरुष समास", "कर्मधारय समास", 0, "लंबा है उदर (पेट) जिनका अर्थात गणेश जी - बहुव्रीहि समास। (Grammar)"),
            QuestionEntity("sec_hn8", "practice_generic_Hindi", "तुलसीदास द्वारा रचित प्रसिद्ध महाकाव्य कौन सा है?", "रामचरितमानस", "महाभारत", "साकेत", "कामायनी", 0, "रामचरितमानस महाकवि गोस्वामी तुलसीदास की प्रसिद्ध रचना है। (Literature)"),
            QuestionEntity("sec_hn9", "practice_generic_Hindi", "अंधे की लाठी मुहावरे का सही अर्थ क्या है?", "एकमात्र सहारा", "मदद न करना", "लाठी खोना", "मार्गदर्शन करना", 0, "अंधे की लाठी अर्थात एकमात्र सहारा होना। (Grammar)"),
            QuestionEntity("sec_hn10", "practice_generic_Hindi", "उत्पत्ति के आधार पर शब्द के कितने भेद होते हैं?", "चार", "दो", "तीन", "पांच", 0, "उत्पत्ति के आधार पर शब्द के चार भेद होते हैं - तत्सम, तद्भव, देशज और विदेशी। (Grammar)"),
            QuestionEntity("sec_hn11", "practice_generic_Hindi", "सूर्य का पर्यायवाची शब्द कौन सा नहीं है?", "शशांक", "दिनकर", "रवि", "भास्कर", 0, "शशांक चंद्रमा का पर्यायवाची है, सूर्य का नहीं। (Grammar)"),
            QuestionEntity("sec_hn12", "practice_generic_Hindi", "कबीर के अनुसार 'साधु' से क्या नहीं पूछना चाहिए?", "उसकी जाति", "उसका ज्ञान", "उसका नाम", "उसका घर", 0, "कबीर के अनुसार 'जाति न पूछो साधु की, पूछ लीजिये ज्ञान।' (Literature)"),
            QuestionEntity("sec_hn13", "practice_generic_Hindi", "अनुराग का सही विलोम शब्द क्या होगा?", "विराग", "प्रेम", "नफ़रत", "राग", 0, "अनुराग का विलोम विराग होता है। (Grammar)"),
            QuestionEntity("sec_hn14", "practice_generic_Hindi", "कारक के कुल कितने भेद होते हैं?", "आठ", "छह", "सात", "नौ", 0, "हिंदी व्याकरण में कारक के आठ भेद होते हैं। (Grammar)"),
            QuestionEntity("sec_hn15", "practice_generic_Hindi", "हरिशंकर परसाई की प्रसिद्ध व्यंग्य रचना कौन सी है?", "प्रेमचंद के फटे जूते", "रीढ़ की हड्डी", "ल्हासा की ओर", "दो बैल", 0, "'प्रेमचंद के फटे जूते' हरिशंकर परसाई की व्यंग्य रचना है। (Literature)")
        ),
        "English" to listOf(
            QuestionEntity("sec_e1", "practice_generic_English", "Identify the part of speech of the underlined word: 'The puppy barked _loudly_.'", "adverb", "verb", "adjective", "noun", 0, "'Loudly' describes how the puppy barked (verb), so it is an adverb. (Grammar)"),
            QuestionEntity("sec_e2", "practice_generic_English", "Who is the author of 'The Road Not Taken'?", "Robert Frost", "William Shakespeare", "Wordsworth", "John Keats", 0, "'The Road Not Taken' is a poem written by Robert Frost. (Literature)"),
            QuestionEntity("sec_e3", "practice_generic_English", "What is the synonym of 'Pragmatic'?", "Practical", "Theoretical", "Idealistic", "Imaginary", 0, "Pragmatic means dealing with things practically. (Grammar)"),
            QuestionEntity("sec_e4", "practice_generic_English", "Who wrote the play 'Julius Caesar'?", "William Shakespeare", "George Bernard Shaw", "Arthur Miller", "T.S. Eliot", 0, "Julius Caesar is a tragedy by William Shakespeare. (Literature)"),
            QuestionEntity("sec_e5", "practice_generic_English", "Complete the sentence: 'She ___ to the market yesterday.'", "went", "goes", "go", "gone", 0, "Yesterday indicates past tense, so 'went' is correct. (Grammar)"),
            QuestionEntity("sec_e6", "practice_generic_English", "Which of these sentences is grammatically correct?", "Neither of the plans is feasible.", "Each of the boys have a book.", "None of the students were ready.", "Every player were happy.", 0, "'Neither' takes a singular verb, so 'is' is correct. (Grammar)"),
            QuestionEntity("sec_e7", "practice_generic_English", "What is the antonym of the word 'Obsolete'?", "Modern", "Ancient", "Outdated", "Useless", 0, "Obsolete means no longer in use, so the antonym is Modern. (Grammar)"),
            QuestionEntity("sec_e8", "practice_generic_English", "Who wrote the famous novel 'Pride and Prejudice'?", "Jane Austen", "Charlotte Bronte", "Mary Shelley", "George Eliot", 0, "Pride and Prejudice was written by Jane Austen. (Literature)"),
            QuestionEntity("sec_e9", "practice_generic_English", "Identify the passive voice of: 'The chef cooked the meal.'", "The meal was cooked by the chef.", "The meal is cooked by the chef.", "The meal has been cooked by the chef.", "The chef has cooked the meal.", 0, "Simple past passive is 'was/were + past participle'. (Grammar)"),
            QuestionEntity("sec_e10", "practice_generic_English", "What is the meaning of the idiom 'spill the beans'?", "To reveal a secret", "To drop food", "To cook dinner", "To work hard", 0, "Spill the beans means to share confidential information. (Grammar)"),
            QuestionEntity("sec_e11", "practice_generic_English", "In 'The Merchant of Venice', who is Portia's suitor who chooses the lead casket?", "Bassanio", "Antonio", "Gratiano", "Lorenzo", 0, "Bassanio correctly selects the lead casket to win Portia. (Literature)"),
            QuestionEntity("sec_e12", "practice_generic_English", "Choose the correct spelling:", "Hierarchy", "Hierarcy", "Heirarchy", "Herarchy", 0, "Hierarchy is the correct spelling. (Grammar)"),
            QuestionEntity("sec_e13", "practice_generic_English", "Complete: 'If it rains, we ___ the match.'", "will cancel", "canceled", "would cancel", "canceling", 0, "First conditional: If + present simple, will + base verb. (Grammar)"),
            QuestionEntity("sec_e14", "practice_generic_English", "Who wrote the poem 'Ode to a Nightingale'?", "John Keats", "Percy Bysshe Shelley", "Lord Byron", "William Wordsworth", 0, "Ode to a Nightingale is one of John Keats's famous 1819 odes. (Literature)"),
            QuestionEntity("sec_e15", "practice_generic_English", "Identify the figure of speech: 'The wind whispered secrets in the dark.'", "Personification", "Metaphor", "Simile", "Alliteration", 0, "Attributing human traits to wind is personification. (Grammar)")
        ),
        "Science" to listOf(
            QuestionEntity("sec_sci1", "practice_generic_Science", "What is the chemical symbol for Gold?", "Au", "Ag", "Fe", "Gd", 0, "Au comes from the Latin name 'Aurum', meaning shining dawn. (Chemistry)"),
            QuestionEntity("sec_sci2", "practice_generic_Science", "Which gas is majorly responsible for global warming?", "Carbon Dioxide", "Oxygen", "Nitrogen", "Helium", 0, "Carbon dioxide (CO2) is the primary greenhouse gas. (Biology/EVS)"),
            QuestionEntity("sec_sci3", "practice_generic_Science", "What is the SI unit of force?", "Newton", "Joule", "Pascal", "Watt", 0, "The Newton (N) is the SI unit of force. (Physics)"),
            QuestionEntity("sec_sci4", "practice_generic_Science", "Which organelle is known as the powerhouse of the cell?", "Mitochondrion", "Nucleus", "Ribosome", "Chloroplast", 0, "Mitochondria produce ATP through cellular respiration. (Biology)"),
            QuestionEntity("sec_sci5", "practice_generic_Science", "What is the atomic number of Carbon?", "6", "12", "8", "4", 0, "Carbon has 6 protons, so its atomic number is 6. (Chemistry)"),
            QuestionEntity("sec_sci6", "practice_generic_Science", "An instrument used to measure electric current is:", "Ammeter", "Voltmeter", "Galvanometer", "Ohmmeter", 0, "An ammeter is connected in series to measure electric current in a circuit. (Physics)"),
            QuestionEntity("sec_sci7", "practice_generic_Science", "Which acid is present in lemon?", "Citric acid", "Acetic acid", "Lactic acid", "Tartaric acid", 0, "Citric acid gives lemons and oranges their sour taste. (Chemistry)"),
            QuestionEntity("sec_sci8", "practice_generic_Science", "The process of conversion of water vapor directly into solid ice is:", "Sublimation", "Condensation", "Evaporation", "Freezing", 0, "Sublimation is transition between solid and gas without passing liquid state. (Chemistry)"),
            QuestionEntity("sec_sci9", "practice_generic_Science", "What type of mirror is used as a shaving mirror?", "Concave mirror", "Convex mirror", "Plane mirror", "Parabolic mirror", 0, "Concave mirrors show enlarged virtual images when close. (Physics)"),
            QuestionEntity("sec_sci10", "practice_generic_Science", "Which blood group is called the universal donor?", "O negative", "AB positive", "A positive", "B negative", 0, "O negative blood lacks A, B, and Rh antigens, making it safe for all. (Biology)"),
            QuestionEntity("sec_sci11", "practice_generic_Science", "What is the value of acceleration due to gravity on Earth?", "9.8 m/s^2", "8.9 m/s^2", "1.6 m/s^2", "10.8 m/s^2", 0, "g is approximately 9.8 meters per second squared on Earth. (Physics)"),
            QuestionEntity("sec_sci12", "practice_generic_Science", "Which gas is filled in electric bulbs to prolong filament life?", "Argon", "Oxygen", "Hydrogen", "Carbon Dioxide", 0, "Inert gases like Argon prevent filaments from burning. (Chemistry)"),
            QuestionEntity("sec_sci13", "practice_generic_Science", "Who proposed the Theory of Evolution by Natural Selection?", "Charles Darwin", "Gregor Mendel", "Lamarck", "Pasteur", 0, "Darwin wrote Origin of Species in 1859 outlining this theory. (Biology)"),
            QuestionEntity("sec_sci14", "practice_generic_Science", "What is the primary constituent of natural gas?", "Methane", "Butane", "Propane", "Ethane", 0, "Methane (CH4) makes up over 85% of natural gas. (Chemistry)"),
            QuestionEntity("sec_sci15", "practice_generic_Science", "Which gland is known as the Master Gland in human body?", "Pituitary gland", "Thyroid gland", "Adrenal gland", "Pancreas", 0, "Pituitary controls other endocrine glands. (Biology)")
        ),
        "SST" to listOf(
            QuestionEntity("sec_sst1", "practice_generic_SST", "Who was the first Prime Minister of independent India?", "Jawaharlal Nehru", "Mahatma Gandhi", "Subhas Chandra Bose", "Sardar Patel", 0, "Jawaharlal Nehru became the first Prime Minister in 1947."),
            QuestionEntity("sec_sst2", "practice_generic_SST", "Which planet is known as the Red Planet?", "Mars", "Venus", "Jupiter", "Saturn", 0, "Mars has an abundance of iron oxide on its surface, giving it a reddish look."),
            QuestionEntity("sec_sst3", "practice_generic_SST", "The Indian Constitution was formally adopted on:", "26th January 1950", "15th August 1947", "26th November 1949", "2nd October 1950", 0, "While adopted on 26 Nov 1949, it came into force on 26 Jan 1950, celebrated as Republic Day."),
            QuestionEntity("sec_sst4", "practice_generic_SST", "Which is the longest river in India?", "Ganga", "Yamuna", "Godavari", "Narmada", 0, "The Ganga is the longest river flowing entirely through India."),
            QuestionEntity("sec_sst5", "practice_generic_SST", "Who is known as the Father of the Indian Constitution?", "Dr. B.R. Ambedkar", "Mahatma Gandhi", "Dr. Rajendra Prasad", "Jawaharlal Nehru", 0, "Dr. B.R. Ambedkar was the chairman of the drafting committee."),
            QuestionEntity("sec_sst6", "practice_generic_SST", "Which Indian state has the highest literacy rate?", "Kerala", "Tamil Nadu", "Maharashtra", "Delhi", 0, "According to census records, Kerala consistently has the highest literacy rate in India."),
            QuestionEntity("sec_sst7", "practice_generic_SST", "Who led the 1857 Revolt against the British in Jhansi?", "Rani Lakshmibai", "Begum Hazrat Mahal", "Sarojini Naidu", "Kasturba Gandhi", 0, "Rani Lakshmibai led the historic uprising of 1857 in Jhansi against British rule."),
            QuestionEntity("sec_sst8", "practice_generic_SST", "Who introduced the doctrine of lapse in India?", "Lord Dalhousie", "Lord Wellesley", "Lord Canning", "Lord Curzon", 0, "Dalhousie used Doctrine of Lapse to annex princely states."),
            QuestionEntity("sec_sst9", "practice_generic_SST", "Which article of Indian Constitution guarantees Right to Equality?", "Article 14", "Article 21", "Article 19", "Article 32", 0, "Article 14 provides equality before law and equal protection of laws."),
            QuestionEntity("sec_sst10", "practice_generic_SST", "Which sector of economy generates highest employment in India?", "Primary Sector", "Secondary Sector", "Tertiary Sector", "Quaternary Sector", 0, "Agriculture (primary) still employs the highest workforce portion in India."),
            QuestionEntity("sec_sst11", "practice_generic_SST", "Where did Mahatma Gandhi start his first Satyagraha in India?", "Champaran", "Kheda", "Ahmedabad", "Dandi", 0, "Champaran Satyagraha in 1917 was Gandhi's first major Indian campaign."),
            QuestionEntity("sec_sst12", "practice_generic_SST", "Which is the largest hot desert in the world?", "Sahara", "Gobi", "Kalahari", "Thar", 0, "The Sahara is the largest hot desert, located in North Africa."),
            QuestionEntity("sec_sst13", "practice_generic_SST", "Who founded the Brahmo Samaj in 1828?", "Raja Ram Mohan Roy", "Swami Dayanand Saraswati", "Mahadev Govind Ranade", "Keshub Chandra Sen", 0, "Raja Ram Mohan Roy founded Brahmo Samaj to fight social evils like Sati."),
            QuestionEntity("sec_sst14", "practice_generic_SST", "How many members are nominated by the President to the Rajya Sabha?", "12", "10", "2", "250", 0, "The President nominates 12 experts in science, art, literature, etc."),
            QuestionEntity("sec_sst15", "practice_generic_SST", "Which pass connects Srinagar to Leh?", "Zoji La", "Rohtang", "Nathu La", "Shipki La", 0, "Zoji La pass connects Srinagar and Leh in western Himalayas.")
        ),
        "GK" to listOf(
            QuestionEntity("sec_gk1", "practice_generic_GK", "Which country is the largest by land area?", "Russia", "Canada", "China", "USA", 0, "Russia is the largest country in the world, covering over 17 million square kilometers."),
            QuestionEntity("sec_gk2", "practice_generic_GK", "What is the currency of Japan?", "Yen", "Won", "Yuan", "Dollar", 0, "The Yen is the official currency of Japan."),
            QuestionEntity("sec_gk3", "practice_generic_GK", "Which organ in the human body pumps blood?", "Heart", "Lungs", "Kidneys", "Liver", 0, "The heart is responsible for circulating blood throughout the body."),
            QuestionEntity("sec_gk4", "practice_generic_GK", "Who painted the Mona Lisa?", "Leonardo da Vinci", "Pablo Picasso", "Vincent van Gogh", "Michelangelo", 0, "The famous portrait Mona Lisa was painted by Leonardo da Vinci."),
            QuestionEntity("sec_gk5", "practice_generic_GK", "Which is the smallest country in the world?", "Vatican City", "Monaco", "San Marino", "Liechtenstein", 0, "Vatican City is the smallest independent state by both area and population."),
            QuestionEntity("sec_gk6", "practice_generic_GK", "The national animal of India is:", "Bengal Tiger", "Asiatic Lion", "Indian Elephant", "Leopard", 0, "The Bengal Tiger (Panthera tigris tigris) is the national animal of India."),
            QuestionEntity("sec_gk7", "practice_generic_GK", "Who was the first human in space?", "Yuri Gagarin", "Neil Armstrong", "Buzz Aldrin", "Rakesh Sharma", 0, "Yuri Gagarin orbit Earth in Vostok 1 in 1961."),
            QuestionEntity("sec_gk8", "practice_generic_GK", "What is the tallest waterfall in the world?", "Angel Falls", "Niagara Falls", "Victoria Falls", "Yosemite Falls", 0, "Angel Falls in Venezuela is the tallest waterfall (979 m)."),
            QuestionEntity("sec_gk9", "practice_generic_GK", "Which country hosts the headquarters of the United Nations (UN)?", "United States", "Switzerland", "United Kingdom", "France", 0, "UN headquarters is located in New York City, US."),
            QuestionEntity("sec_gk10", "practice_generic_GK", "Which gas is most abundant in the Earth's atmosphere?", "Nitrogen", "Oxygen", "Argon", "Carbon Dioxide", 0, "Nitrogen makes up roughly 78% of dry air."),
            QuestionEntity("sec_gk11", "practice_generic_GK", "Who is known as the Iron Man of India?", "Sardar Vallabhbhai Patel", "Subhas Chandra Bose", "Lal Bahadur Shastri", "Bhagat Singh", 0, "Patel is called Iron Man for his crucial role in princely integration."),
            QuestionEntity("sec_gk12", "practice_generic_GK", "Which is the largest organ in the human body?", "Skin", "Liver", "Lungs", "Brain", 0, "The skin is the largest external organ of the human body."),
            QuestionEntity("sec_gk13", "practice_generic_GK", "Who discovered penicillin?", "Alexander Fleming", "Louis Pasteur", "Edward Jenner", "Robert Koch", 0, "Fleming discovered penicillin in 1928 at St. Mary's Hospital."),
            QuestionEntity("sec_gk14", "practice_generic_GK", "What is the capital of Australia?", "Canberra", "Sydney", "Melbourne", "Brisbane", 0, "Canberra is the designated capital of Australia."),
            QuestionEntity("sec_gk15", "practice_generic_GK", "In which year did the Titanic sink?", "1912", "1905", "1922", "1899", 0, "Titanic hit an iceberg and sank on its maiden voyage in April 1912.")
        ),
        "Computer" to listOf(
            QuestionEntity("sec_comp1", "practice_generic_Computer", "What does CPU stand for?", "Central Processing Unit", "Central Program Unit", "Computer Processing Unit", "Core Power Unit", 0, "CPU is the primary component of a computer that acts as its brain."),
            QuestionEntity("sec_comp2", "practice_generic_Computer", "Which of the following is a primary pointing input device?", "Mouse", "Monitor", "Printer", "Speaker", 0, "A mouse is the primary pointing device used to input instructions by clicking."),
            QuestionEntity("sec_comp3", "practice_generic_Computer", "One kilobyte (KB) is equal to how many bytes?", "1024 bytes", "1000 bytes", "1048 bytes", "512 bytes", 0, "In binary computer storage, 1 KB is 1024 bytes."),
            QuestionEntity("sec_comp4", "practice_generic_Computer", "What is the main operating system developed by Microsoft?", "Windows", "macOS", "Linux", "Android", 0, "Windows is Microsoft's proprietary family of operating systems."),
            QuestionEntity("sec_comp5", "practice_generic_Computer", "Which protocol is used to access web pages on the internet?", "HTTP", "FTP", "SMTP", "SSH", 0, "Hypertext Transfer Protocol (HTTP) is used to load web pages."),
            QuestionEntity("sec_comp6", "practice_generic_Computer", "What type of storage is RAM (Random Access Memory)?", "Volatile (Temporary)", "Non-volatile (Permanent)", "Read-only", "Sequential", 0, "RAM is volatile memory; its contents are lost when the device is powered off."),
            QuestionEntity("sec_comp7", "practice_generic_Computer", "Which language is interpreted directly by a computer?", "Machine Language", "Assembly Language", "C++", "Java", 0, "Machine language consists of binary code executed directly by CPU."),
            QuestionEntity("sec_comp8", "practice_generic_Computer", "What is the main storage component of a computer?", "Hard Disk Drive", "RAM", "Cache Memory", "Registers", 0, "HDD/SSD holds permanent OS, applications, and user files."),
            QuestionEntity("sec_comp9", "practice_generic_Computer", "What does HTML stand for?", "Hypertext Markup Language", "Hypertext Makeup Language", "Hyperlink Text Markup Language", "Home Text Markup Language", 0, "HTML is standard markup language for creating web pages."),
            QuestionEntity("sec_comp10", "practice_generic_Computer", "Which component holds the CPU, RAM, and other expansion slots?", "Motherboard", "Chassis", "SMPS", "Hard Disk", 0, "The motherboard is the main printed circuit board connecting components."),
            QuestionEntity("sec_comp11", "practice_generic_Computer", "What does URL stand for?", "Uniform Resource Locator", "Universal Resource Locator", "Unique Resource Locator", "Uniform Resource Link", 0, "URL is the address of a resource on the web."),
            QuestionEntity("sec_comp12", "practice_generic_Computer", "What is malware designed to copy itself and infect other files?", "Virus", "Spyware", "Adware", "Firewall", 0, "A computer virus replicates by attaching to host files."),
            QuestionEntity("sec_comp13", "practice_generic_Computer", "Which database language is standard for querying relational tables?", "SQL", "HTML", "CSS", "C#", 0, "Structured Query Language (SQL) manages and updates relational DBs."),
            QuestionEntity("sec_comp14", "practice_generic_Computer", "What is the primary function of a Firewall?", "To block unauthorized access", "To speed up internet", "To clean up viruses", "To store backups", 0, "Firewalls monitor and filter network traffic based on security rules."),
            QuestionEntity("sec_comp15", "practice_generic_Computer", "Who is known as the Father of Computers?", "Charles Babbage", "Alan Turing", "John von Neumann", "Ada Lovelace", 0, "Charles Babbage designed the Analytical Engine, the mechanical computer precursor.")
        ),
        "Sanskrit" to listOf(
            QuestionEntity("sec_sans1", "practice_generic_Sanskrit", "वर्णमालायां कति स्वराः भवन्ति?", "13 (त्रयोदश)", "11 (एकादश)", "33 (त्रयस्त्रिंशत्)", "5 (पञ्च)", 0, "संस्कृत वर्णमाला में 13 स्वर माने गए हैं।"),
            QuestionEntity("sec_sans2", "practice_generic_Sanskrit", "'बालकः' इति पदे का विभक्तिः अस्ति?", "प्रथमा", "द्वितीया", "तृतीया", "षष्ठी", 0, "बालकः अकारान्त पुल्लिंग शब्द का प्रथमा विभक्ति एकवचन है।"),
            QuestionEntity("sec_sans3", "practice_generic_Sanskrit", "'पठति' क्रियापदे कः लकारः अस्ति?", "लट्लकारः", "लोट्लकारः", "लृट्लकारः", "लङ्लकारः", 0, "पठति में वर्तमान काल के लिए लट् लकार है।"),
            QuestionEntity("sec_sans4", "practice_generic_Sanskrit", "सन्धिविच्छेदं कुरुत: 'सूर्योदयः'?", "सूर्य + उदयः", "सूर्यो + दयः", "सूर + उदयः", "सूर्य + ओदयः", 0, "सूर्य + उदयः = सूर्योदयः (गुण स्वर संधि)।"),
            QuestionEntity("sec_sans5", "practice_generic_Sanskrit", "'गच्छति' इति पदे कः धातुः अस्ति?", "गम्", "गच्छ्", "चल", "पठ", 0, "गच्छति पद में मूल धातु 'गम्' (जाना) है।"),
            QuestionEntity("sec_sans6", "practice_generic_Sanskrit", "संस्कृतभाषायाः आदिग्रन्थः कः मन्यते?", "ऋग्वेदः", "रामायणम्", "महाभारतम्", "सामवेदः", 0, "ऋग्वेद संसार का आदिग्रन्थ माना जाता है।"),
            QuestionEntity("sec_sans7", "practice_generic_Sanskrit", "'लतातः' पदे का विभक्तिः अस्ति?", "पञ्चमी", "द्वितीया", "तृतीया", "सप्तमी", 0, "लतातः में पञ्चमी विभक्ति एकवचन का रूप है।"),
            QuestionEntity("sec_sans8", "practice_generic_Sanskrit", "'त्वम्' इति सर्वनामपदं कस्मिन् पुरुषे अस्ति?", "मध्यमपुरुषे", "प्रथमपुरुषे", "उत्तमपुरुषे", "अन्यपुरुषे", 0, "त्वम् (तुम) युष्मद् शब्द का मध्यम पुरुष है।"),
            QuestionEntity("sec_sans9", "practice_generic_Sanskrit", "'रामायणम्' महाकाव्यस्य रचयिता कः?", "महर्षि वाल्मीकिः", "वेदव्यासः", "कालिदासः", "भासः", 0, "रामायण महाकाव्य की रचना महर्षि वाल्मीकि ने की थी।"),
            QuestionEntity("sec_sans10", "practice_generic_Sanskrit", "अष्टाध्यायी व्याकरण ग्रन्थस्य रचयिता कः?", "महर्षि पाणिनिः", "पतञ्जलिः", "वरदराजः", "कात्यायनः", 0, "अष्टाध्यायी की रचना व्याकरण मुनि महर्षि पाणिनि ने की।"),
            QuestionEntity("sec_sans11", "practice_generic_Sanskrit", "‘विद्या + आलयः’ इत्यस्य सन्धियुक्तं पदं किम्?", "विद्यालयः", "विद्यालायः", "विद्य्यालयः", "विद्याआलयः", 0, "विद्या + आलयः = विद्यालयः (दीर्घ स्वर संधि)।"),
            QuestionEntity("sec_sans12", "practice_generic_Sanskrit", "‘फलम्’ शब्दस्य द्वितीया विभक्ति बहुवचनं किम्?", "फलानि", "फले", "फलम्", "फलैः", 0, "नपुंसकलिंग फल शब्द का द्वितीया बहुवचन 'फलानि' होता है।"),
            QuestionEntity("sec_sans13", "practice_generic_Sanskrit", "‘हसितुम्’ इत्यत्र कः प्रत्ययः प्रयुक्तः?", "तुमुन्", "क्त्वा", "ल्यप्", "क्त", 0, "हस् धातु में लट्/तुमुन् प्रत्यय लगाने से हसितुम् बनता है।"),
            QuestionEntity("sec_sans14", "practice_generic_Sanskrit", "कः संस्कृत भाषायाः प्रसिद्धः कविः अस्ति?", "कालिदासः", "तुलसीदासः", "सूरदासः", "कबीरः", 0, "महाकवि कालिदास संस्कृत साहित्य के सर्वश्रेष्ठ कवि माने जाते हैं।"),
            QuestionEntity("sec_sans15", "practice_generic_Sanskrit", "‘पठन्तु’ क्रियापदं कस्मिन् लकारे अस्ति?", "लोट्लकारे", "लट्लकारे", "लृट्लकारे", "लङ्लकारे", 0, "पठन्तु पठ् धातु का लोट् लकार (आज्ञार्थक) प्रथम पुरुष बहुवचन है।")
        ),
        "Math" to listOf(
            QuestionEntity("sec_m1", "practice_generic_Math", "What is 15% of 200?", "30", "20", "15", "40", 0, "15% of 200 = 15/100 * 200 = 30."),
            QuestionEntity("sec_m2", "practice_generic_Math", "If x + 7 = 15, then 2x is:", "16", "8", "14", "18", 0, "x = 15 - 7 = 8, so 2x = 16."),
            QuestionEntity("sec_m3", "practice_generic_Math", "The value of pi is approximately:", "3.14159", "2.17828", "1.41421", "1.73205", 0, "pi is approximately equal to 3.14159..."),
            QuestionEntity("sec_m4", "practice_generic_Math", "A triangle with all three sides equal is called:", "Equilateral", "Isosceles", "Scalene", "Right-angled", 0, "A triangle with all sides equal is an equilateral triangle."),
            QuestionEntity("sec_m5", "practice_generic_Math", "What is the average of 10, 20, and 30?", "20", "15", "25", "30", 0, "Average = (10+20+30)/3 = 60/3 = 20."),
            QuestionEntity("sec_m6", "practice_generic_Math", "Find the value of sqrt(144) + sqrt(25):", "17", "13", "19", "15", 0, "sqrt(144) = 12, sqrt(25) = 5. 12 + 5 = 17."),
            QuestionEntity("sec_m7", "practice_generic_Math", "Find the slope of a line parallel to y = 3x - 5.", "3", "-3", "1/3", "-1/3", 0, "Parallel lines have equal slopes."),
            QuestionEntity("sec_m8", "practice_generic_Math", "Find the value of 25% of 80.", "20", "15", "25", "30", 0, "25% of 80 = (25 / 100) * 80 = 20."),
            QuestionEntity("sec_m9", "practice_generic_Math", "What is the sum of first 10 odd numbers?", "100", "50", "90", "110", 0, "Sum of first n odd numbers = n^2 = 10^2 = 100."),
            QuestionEntity("sec_m10", "practice_generic_Math", "A car travels 120 km in 2 hours. What is its average speed?", "60 km/h", "50 km/h", "40 km/h", "70 km/h", 0, "Speed = Distance / Time = 120 km / 2 hours = 60 km/h."),
            QuestionEntity("sec_m11", "practice_generic_Math", "If two coins are tossed, find probability of getting two heads.", "1/4", "1/2", "3/4", "1", 0, "Outcomes: HH, HT, TH, TT. P(HH) = 1/4."),
            QuestionEntity("sec_m12", "practice_generic_Math", "What is the area of a circle in terms of diameter d?", "(pi * d^2)/4", "pi * d^2", "2 * pi * d", "pi * d", 0, "Area = pi * r^2 = pi * (d/2)^2 = (pi * d^2)/4."),
            QuestionEntity("sec_m13", "practice_generic_Math", "If the volume of a cube is 64 cc, find its edge length.", "4 cm", "8 cm", "6 cm", "16 cm", 0, "Volume = side^3 = 64, side = 4."),
            QuestionEntity("sec_m14", "practice_generic_Math", "What is the probability of rolling an even number on a fair 6-sided die?", "1/2", "1/3", "1/6", "2/3", 0, "Even numbers on a die are {2, 4, 6} (3 outcomes out of 6). P = 3/6 = 1/2."),
            QuestionEntity("sec_m15", "practice_generic_Math", "What is the LCM of 15 and 20?", "60", "30", "45", "90", 0, "Multiples of 20: 20, 40, 60... 60 is divisible by 15. LCM is 60.")
        )
    )

    // Senior secondary school age fallback (Class 11 to 12)
    val seniorSubjectQuestions = mapOf(
        "Hindi" to listOf(
            QuestionEntity("sen_hn1", "practice_generic_Hindi", "'अलंकार' शब्द का शाब्दिक अर्थ क्या होता है?", "आभूषण", "वस्त्र", "सौंदर्य", "काव्य", 0, "काव्य की शोभा बढ़ाने वाले तत्व को अलंकार कहते हैं, जिसका शाब्दिक अर्थ आभूषण होता है। (CBSE Hindi Core)"),
            QuestionEntity("sen_hn2", "practice_generic_Hindi", "'आरोह भाग-2' में संकलित 'आत्मपरिचय' कविता के रचयिता कौन हैं?", "हरिवंश राय बच्चन", "सूर्यकांत त्रिपाठी 'निराला'", "रघुवीर सहाय", "कुंवर नारायण", 0, "'आत्मपरिचय' कविता प्रसिद्ध कवि हरिवंश राय बच्चन द्वारा रचित है। (CBSE Class 12 Aroh)"),
            QuestionEntity("sen_hn3", "practice_generic_Hindi", "जहाँ उपमेय में उपमान की संभावना की जाए, वहाँ कौन सा अलंकार होता है?", "उत्प्रेक्षा अलंकार", "उपमा अलंकार", "रूपक अलंकार", "श्लेष अलंकार", 0, "मनु, मानों, जनु, जानों आदि वाचक शब्दों के साथ संभावना व्यक्त करने पर उत्प्रेक्षा अलंकार होता है। (Grammar)"),
            QuestionEntity("sen_hn4", "practice_generic_Hindi", "मुंशी प्रेमचंद के किस उपन्यास को 'कृषक जीवन का महाकाव्य' कहा जाता है?", "गोदान", "गबन", "निर्मला", "रंगभूमि", 0, "गोदान भारतीय ग्रामीण समाज और कृषक जीवन की सजीव झांकी प्रस्तुत करता है। (Literature)"),
            QuestionEntity("sen_hn5", "practice_generic_Hindi", "रस निष्पत्ति के संदर्भ में स्थायी भावों की संख्या मूलतः कितनी मानी गई है?", "9 (नौ)", "11 (ग्यारह)", "8 (आठ)", "10 (दस)", 0, "भरतमुनि के नाट्यशास्त्र और शास्त्रीय परंपरा में मूलतः 9 स्थायी भाव और 9 रस माने गए हैं। (Grammar)"),
            QuestionEntity("sen_hn6", "practice_generic_Hindi", "'पतंग' कविता के रचयिता कौन हैं?", "आलोक धन्वा", "शमशेर बहादुर सिंह", "गजानन माधव मुक्तिबोध", "विष्णु खरे", 0, "आलोक धन्वा की प्रसिद्ध कविता 'पतंग' बच्चों की उमंगों का सुंदर चित्रण करती है। (CBSE Class 12)"),
            QuestionEntity("sen_hn7", "practice_generic_Hindi", "पत्रकारिता के कितने ककार (Questions) माने जाते हैं?", "6 (छह)", "5 (पाँच)", "4 (चार)", "7 (सात)", 0, "समाचार लेखन में छह ककार (क्या, कौन, कहाँ, कब, क्यों, कैसे) होते हैं। (Abhivyakti Aur Madhyam)"),
            QuestionEntity("sen_hn8", "practice_generic_Hindi", "'सिल्वर वैडिंग' कहानी के मुख्य पात्र का नाम क्या है?", "यशोधर पंत (यशोधर बाबू)", "किशनदा", "भूषण", "चड्ढा", 0, "मनोहर श्याम जोशी द्वारा रचित 'सिल्वर वैडिंग' के नायक यशोधर बाबू हैं। (CBSE Class 12 Vitan)"),
            QuestionEntity("sen_hn9", "practice_generic_Hindi", "'चौपाई' छंद के प्रत्येक चरण में कितनी मात्राएँ होती हैं?", "16 (सोलह)", "11 (ग्यारह)", "13 (तेरह)", "24 (चौबीस)", 0, "चौपाई एक सम मात्रिक छंद है जिसके प्रत्येक चरण में 16 मात्राएँ होती हैं। (Grammar)"),
            QuestionEntity("sen_hn10", "practice_generic_Hindi", "मुहावरे 'दाल न गलना' का सटीक अर्थ क्या है?", "सफलता न मिलना / युक्ति सफल न होना", "भोजन न पकना", "झगड़ा होना", "हार स्वीकार करना", 0, "दाल न गलना अर्थात किसी काम या उद्देश्य में युक्ति का सफल न होना। (Grammar)"),
            QuestionEntity("sen_hn11", "practice_generic_Hindi", "'भक्तिन' संस्मरणात्मक रेखाचित्र की लेखिका कौन हैं?", "महादेवी वर्मा", "सुभद्रा कुमारी चौहान", "कृष्णा सोबती", "मन्नू भंडारी", 0, "महादेवी वर्मा ने अपनी सेविका भक्तिन के जीवन पर यह मार्मिक रेखाचित्र लिखा है। (CBSE Aroh)"),
            QuestionEntity("sen_hn12", "practice_generic_Hindi", "निम्नलिखित में से कौन सा शब्द तत्सम है?", "अग्नि", "आग", "सूरज", "खेत", 0, "अग्नि संस्कृत से ज्यों का त्यों हिंदी में प्रयुक्त हुआ तत्सम शब्द है। (Grammar)"),
            QuestionEntity("sen_hn13", "practice_generic_Hindi", "विशेषण के मुख्य रूप से कितने भेद होते हैं?", "4 (चार)", "3 (तीन)", "5 (पाँच)", "6 (छह)", 0, "गुणवाचक, संख्यावाचक, परिमाणवाचक और सार्वनामिक विशेषण - चार प्रमुख भेद हैं। (Grammar)"),
            QuestionEntity("sen_hn14", "practice_generic_Hindi", "'जूझ' उपन्यास के लेखक कौन हैं?", "आनंद यादव", "मनोहर श्याम जोशी", "ओम थानवी", "फणीश्वरनाथ रेणु", 0, "डॉ. आनंद यादव द्वारा लिखित आत्मकथात्मक उपन्यास 'जूझ' है। (CBSE Class 12 Vitan)"),
            QuestionEntity("sen_hn15", "practice_generic_Hindi", "फीचर लेखन की शैली कैसी होती है?", "कथात्मक एवं रोचक", "उल्टा पिरामिड शैली", "केवल औपचारिक", "संक्षिप्त विवरण", 0, "फीचर लेखन में कथात्मक, रचनात्मक एवं मनोरंजक शैली का प्रयोग किया जाता है। (Abhivyakti Aur Madhyam)")
        ),
        "Sanskrit" to listOf(
            QuestionEntity("sen_san1", "practice_generic_Sanskrit", "संस्कृत साहित्ये 'कविकुलगुरुः' इति उपाधिना कः विभूषितः?", "महाकवि कालिदासः", "बाणभट्टः", "भवभूतिः", "दण्डी", 0, "महाकवि कालिदासः कविकुलगुरुः इति नाम्ना प्रसिद्धः अस्ति। (CBSE Sanskrit Core)"),
            QuestionEntity("sen_san2", "practice_generic_Sanskrit", "'अभिज्ञानशाकुन्तलम्' कस्य प्रसिद्धं नाटकम् अस्ति?", "कालिदासस्य", "भासस्य", "विशाखदत्तस्य", "हर्षवर्धनस्य", 0, "अभिज्ञानशाकुन्तलम् महाकवि कालिदासस्य विश्वप्रसिद्धं नाटकम् अस्ति। (Sanskrit Literature)"),
            QuestionEntity("sen_san3", "practice_generic_Sanskrit", "'अनुष्टुप्' छन्दसि प्रत्येकस्मिन् पादे कति वर्णाः भवन्ति?", "8 (अष्टौ)", "11 (एकादश)", "12 (द्वादश)", "14 (चतुर्दश)", 0, "अनुष्टुप् छन्दसि चत्वारः पादाः भवन्ति, प्रत्येकस्मिन् पादे 8 वर्णाः (कुल 32 वर्णाः) सन्ति। (Chhanda)"),
            QuestionEntity("sen_san4", "practice_generic_Sanskrit", "'इको यणचि' सूत्रेण का सन्धिः विधीयते?", "यण् सन्धिः", "गुण सन्धिः", "दीर्घ सन्धिः", "अयादि सन्धिः", 0, "इक् प्रत्याहारस्य स्थाने यण् प्रत्याहारः भवति अचि परे। (Panini Grammar)"),
            QuestionEntity("sen_san5", "practice_generic_Sanskrit", "'कादम्बरी' गद्यकाव्यस्य रचयिता कः अस्ति?", "बाणभट्टः", "दण्डी", "सुबन्धुः", "अम्बिकादत्तव्यासः", 0, "बाणभट्टः संस्कृत गद्यसाहित्यस्य मूर्धन्यः कविः अस्ति। (Literature)"),
            QuestionEntity("sen_san6", "practice_generic_Sanskrit", "'पितरौ' इत्यत्र कः समासः अस्ति?", "एकशेष द्वन्द्व समासः", "तत्पुरुष समासः", "बहुव्रीहि समासः", "अव्ययीभाव समासः", 0, "माता च पिता च = पितरौ (एकशेष द्वन्द्वः)। (Grammar)"),
            QuestionEntity("sen_san7", "practice_generic_Sanskrit", "‘हितोपदेशः’ ग्रन्थस्य रचयिता कः?", "नारायण पण्डितः", "विष्णु शर्मा", "शूद्रकः", "चाणक्यः", 0, "हितोपदेशस्य रचयिता नारायण पण्डितः अस्ति। (Literature)"),
            QuestionEntity("sen_san8", "practice_generic_Sanskrit", "'पठेत्' इति क्रियापदं कस्मिन् लकारे अस्ति?", "विधिलिङ् लकारे", "लोट् लकारे", "लट् लकारे", "लङ् लकारे", 0, "पठेत् इति रूपं विधिलिङ् लकारे (चाहिए अर्थे) प्रयुज्यते। (Grammar)"),
            QuestionEntity("sen_san9", "practice_generic_Sanskrit", "संस्कृते कारकाणि कति भवन्ति?", "षट् (6)", "सप्त (7)", "अष्ट (8)", "पञ्च (5)", 0, "क्रियान्वयि कारकम् - संस्कृते षट् कारकाणि मतानि (सम्बन्धसम्बोधने न गण्येते)। (Grammar)"),
            QuestionEntity("sen_san10", "practice_generic_Sanskrit", "'उपमेय-उपमानयोः अभेदवर्णनं' कः अलंकारः?", "रूपक अलंकारः", "उपमा अलंकारः", "उत्प्रेक्षा अलंकारः", "अर्थान्तरन्यासः", 0, "उपमेय और उपमान में अभिन्नता दिखाने पर रूपक अलंकार होता है। (Alankara)"),
            QuestionEntity("sen_san11", "practice_generic_Sanskrit", "'नीतिशतकम्' ग्रन्थस्य रचयिता कः अस्ति?", "भर्तृहरिः", "कालिदासः", "अममरुकः", "जयदेवः", 0, "शतकत्रय (नीति, शृंगार, वैराग्य) रचयिता राजा भर्तृहरिः अस्ति। (Literature)"),
            QuestionEntity("sen_san12", "practice_generic_Sanskrit", "‘प्रत्येकम्’ पदे कः उपसर्गः अस्ति?", "प्रति", "प्र", "परि", "परा", 0, "प्रति + एकम् = प्रत्येकम् (यण् सन्धिः, प्रति उपसर्गः)। (Grammar)"),
            QuestionEntity("sen_san13", "practice_generic_Sanskrit", "‘मुद्राराक्षसम्’ नाटकस्य रचयिता कः?", "विशाखदत्तम्", "शूद्रकः", "भासः", "भवभूतिः", 0, "विशाखदत्तः राजनीतिक नाटकस्य मुद्राराक्षसस्य रचयिता अस्ति। (Literature)"),
            QuestionEntity("sen_san14", "practice_generic_Sanskrit", "‘भगवद्गीता’ महाभारतस्य कस्मिन् पर्वे अस्ति?", "भीष्मपर्वे", "उद्योगपर्वे", "शान्तिपर्वे", "विराटपर्वे", 0, "श्रीमद्भगवद्गीता महाभारतस्य भीष्मपर्वणि संकलिता अस्ति। (Indian Heritage)"),
            QuestionEntity("sen_san15", "practice_generic_Sanskrit", "‘गमनीया’ पदे कः प्रत्ययः प्रयुक्तः?", "अनीयर् प्रत्ययः", "तव्यत् प्रत्ययः", "क्त्व प्रत्ययः", "शतृ प्रत्ययः", 0, "गम् + अनीयर् (स्त्रीलिङ्गे टाप्) = गमनीया। (Grammar)")
        ),
        "English" to listOf(
            QuestionEntity("sen_eng1", "practice_generic_English", "Which figure of speech is used in the phrase 'as busy as a bee'?", "Simile", "Metaphor", "Personification", "Hyperbole", 0, "A simile compares two things using 'like' or 'as'. (Grammar)"),
            QuestionEntity("sen_eng2", "practice_generic_English", "Who is the creator of the character 'Hamlet'?", "William Shakespeare", "John Milton", "Leo Tolstoy", "Christopher Marlowe", 0, "Hamlet is a tragedy written by William Shakespeare. (Literature)"),
            QuestionEntity("sen_eng3", "practice_generic_English", "What is the synonym of the word 'Pragmatic'?", "Practical", "Theoretical", "Idealistic", "Imaginary", 0, "Pragmatic means dealing with things realistically. (Grammar)"),
            QuestionEntity("sen_eng4", "practice_generic_English", "Who is the author of the poem 'Paradise Lost'?", "John Milton", "Dante", "Homer", "Virgil", 0, "Paradise Lost is an epic poem in blank verse by John Milton. (Literature)"),
            QuestionEntity("sen_eng5", "practice_generic_English", "Which of the following represents the subjunctive mood?", "If I were you, I would study.", "I am studying now.", "Study hard for the exam.", "He studies every day.", 0, "'If I were you' uses 'were' for a hypothetical condition. (Grammar)"),
            QuestionEntity("sen_eng6", "practice_generic_English", "Identify the grammatically correct sentence:", "None of the students has finished the assignment.", "Each of the boys have a book.", "Neither of the plans are feasible.", "Every one of the players were ready.", 0, "'None' typically takes a singular verb ('has finished') when referring to a singular entity. (Grammar)"),
            QuestionEntity("sen_eng7", "practice_generic_English", "What is the meaning of the word 'Anachronism'?", "Something out of its proper time period", "A type of spelling error", "A figure of speech", "A character description", 0, "Anachronism matches something placed in chronological error. (Grammar)"),
            QuestionEntity("sen_eng8", "practice_generic_English", "Who wrote the dystopian novel '1984'?", "George Orwell", "Aldous Huxley", "Ray Bradbury", "F. Scott Fitzgerald", 0, "1984 is a classic social science fiction novel by George Orwell. (Literature)"),
            QuestionEntity("sen_eng9", "practice_generic_English", "Complete: 'By the time she arrives, we ___ dinner.'", "will have finished", "finished", "finish", "will finish", 0, "Future perfect tense indicates action completed by a future point. (Grammar)"),
            QuestionEntity("sen_eng10", "practice_generic_English", "What is the antonym of 'Transient'?", "Permanent", "Temporary", "Fleeting", "Short-lived", 0, "Transient means lasting a short time, so Permanent is the antonym. (Grammar)"),
            QuestionEntity("sen_eng11", "practice_generic_English", "In Keats's 'Ode on a Grecian Urn', what is the ultimate truth?", "Beauty is truth, truth beauty.", "Love conquereth all.", "Life is fleeting.", "Nature is divine.", 0, "The urn expresses: 'Beauty is truth, truth beauty,-that is all Ye know on earth...' (Literature)"),
            QuestionEntity("sen_eng12", "practice_generic_English", "Choose the correct spellings of the following:", "Exaggerate", "Exagerate", "Exaggerat", "Exagerat", 0, "Exaggerate is the correct spelling. (Grammar)"),
            QuestionEntity("sen_eng13", "practice_generic_English", "What is a 'Soliloquy'?", "A character speaking their thoughts aloud alone", "A dialogue between two characters", "The opening scene of a play", "A poem written in 14 lines", 0, "Soliloquy is speaking thoughts aloud regardless of listeners, typical in drama. (Literature)"),
            QuestionEntity("sen_eng14", "practice_generic_English", "Identify the type of clause: 'Although he was tired, he finished the work.'", "Adverbial clause", "Noun clause", "Adjective clause", "Relative clause", 0, "'Although he was tired' is an adverbial clause of concession. (Grammar)"),
            QuestionEntity("sen_eng15", "practice_generic_English", "Who wrote the American classic novel 'The Great Gatsby'?", "F. Scott Fitzgerald", "Ernest Hemingway", "William Faulkner", "Mark Twain", 0, "F. Scott Fitzgerald wrote 'The Great Gatsby' in 1925. (Literature)")
        ),
        "Physics" to listOf(
            QuestionEntity("sen_phy1", "practice_generic_Physics", "The electrostatic force between two point charges is governed by:", "Coulomb's Law", "Ohm's Law", "Faraday's Law", "Ampere's Law", 0, "Coulomb's Law states that electrostatic force is proportional to product of charges and inversely to square of distance."),
            QuestionEntity("sen_phy2", "practice_generic_Physics", "SI unit of electrical conductivity is:", "Siemens per meter", "Ohm meter", "Siemens", "Ohm", 0, "Conductivity is the reciprocal of resistivity. Unit is S/m or mho/m."),
            QuestionEntity("sen_phy3", "practice_generic_Physics", "Which element is commonly used to make the core of a transformer?", "Soft Iron", "Copper", "Steel", "Aluminium", 0, "Soft iron is used because of its high permeability and low magnetic retentivity/coercivity to reduce hysteresis loss."),
            QuestionEntity("sen_phy4", "practice_generic_Physics", "The focal length of a concave lens is always:", "Negative", "Positive", "Zero", "Infinite", 0, "According to sign conventions, the focal length of a concave lens is negative."),
            QuestionEntity("sen_phy5", "practice_generic_Physics", "In a photoelectric effect experiment, if frequency of light increases:", "Stopping potential increases", "Photocurrent increases", "Work function increases", "Stopping potential decreases", 0, "K_max = hf - phi. Since kinetic energy of emitted photoelectrons increases, stopping potential increases."),
            QuestionEntity("sen_phy6", "practice_generic_Physics", "The depletion region in a p-n junction diode contains:", "Immobile ions", "Free electrons", "Holes", "Neutral atoms", 0, "The depletion region consists of uncompensated immobile donor and acceptor ions."),
            QuestionEntity("sen_phy7", "practice_generic_Physics", "What is the work function of a metal?", "Minimum energy required to eject an electron", "Maximum energy of photoelectrons", "Energy of incident photons", "Threshold frequency", 0, "Work function is the minimum energy required to liberate an electron from metal surface."),
            QuestionEntity("sen_phy8", "practice_generic_Physics", "Which wave theory did Christiaan Huygens propose?", "Wave theory of light", "Corpuscular theory", "Electromagnetic wave theory", "Quantum theory", 0, "Huygens proposed wave theory of light in 1678, explaining reflection/refraction."),
            QuestionEntity("sen_phy9", "practice_generic_Physics", "What is the relation between power and focal length of a lens?", "P = 1/f (in meters)", "P = f", "P = 100/f (in meters)", "P = f^2", 0, "Power is reciprocal of focal length in meters (unit: Diopter)."),
            QuestionEntity("sen_phy10", "practice_generic_Physics", "The self-inductance of a solenoid is proportional to:", "Square of turns (N^2)", "Number of turns (N)", "Length of solenoid (L)", "Radius (r)", 0, "L = (mu_o * N^2 * A)/l. Thus inductance is proportional to N^2."),
            QuestionEntity("sen_phy11", "practice_generic_Physics", "Electromagnetic waves are produced by:", "Accelerated charges", "Charges at rest", "Charges in uniform motion", "Neutral particles", 0, "An accelerated charge produces varying electric and magnetic fields creating EM waves."),
            QuestionEntity("sen_phy12", "practice_generic_Physics", "Which electromagnetic radiation has the highest frequency?", "Gamma rays", "X-rays", "Ultraviolet rays", "Radio waves", 0, "Gamma rays have the shortest wavelength and highest frequency/energy in EM spectrum."),
            QuestionEntity("sen_phy13", "practice_generic_Physics", "What is the angle of deviation for a light ray grazing prism surface?", "Maximum deviation", "Minimum deviation", "Zero", "No deviation", 0, "Grazing incidence leads to maximum angle of prism deviation."),
            QuestionEntity("sen_phy14", "practice_generic_Physics", "Brewster's angle is related to refractive index by:", "mu = tan(i_p)", "mu = sin(i_p)", "mu = cos(i_p)", "mu = cot(i_p)", 0, "Brewster's law states that tangent of polarizing angle is equal to refractive index."),
            QuestionEntity("sen_phy15", "practice_generic_Physics", "The energy of a photon of wavelength lambda is given by:", "hc / lambda", "h * lambda", "h * c * lambda", "h / lambda", 0, "E = f = hc/lambda, where h is Planck's constant and c is speed of light.")
        ),
        "Chemistry" to listOf(
            QuestionEntity("sen_ch1", "practice_generic_Chemistry", "What is the coordination number of atoms in a body-centered cubic (BCC) structure?", "8", "6", "12", "4", 0, "In a BCC lattice, the central atom touches 8 corner atoms, so coordination number is 8."),
            QuestionEntity("sen_ch2", "practice_generic_Chemistry", "The rate constant of a first-order reaction depends on:", "Temperature", "Reactant concentration", "Time", "Product concentration", 0, "Rate constant depends on temperature according to the Arrhenius equation."),
            QuestionEntity("sen_ch3", "practice_generic_Chemistry", "Which of the following transition metal ions forms a colored compound?", "Iron(II)", "Scandium(III)", "Titanium(IV)", "Zinc(II)", 0, "Iron (Fe) has an incomplete d-subshell permitting d-d transitions, whereas the others have empty or filled d-orbitals."),
            QuestionEntity("sen_ch4", "practice_generic_Chemistry", "Which chemical test is used to distinguish primary, secondary, and tertiary alcohols?", "Lucas Test", "Tollens Test", "Fehling Test", "Biuret Test", 0, "Lucas reagent (conc. HCl + anhydrous ZnCl2) distinguishes alcohols based on turbidity rates."),
            QuestionEntity("sen_ch5", "practice_generic_Chemistry", "Which of the following is a natural polymer?", "Cellulose", "Nylon-6,6", "Terylene", "Buna-S", 0, "Cellulose is a natural polymer found in plants, whereas the others are synthetic polymers."),
            QuestionEntity("sen_ch6", "practice_generic_Chemistry", "The oxidation state of cobalt in [Co(NH3)6]Cl3 is:", "+3", "+2", "+6", "0", 0, "NH3 is neutral. Cl carries -1. So Co + 6(0) + 3(-1) = 0 -> Co = +3."),
            QuestionEntity("sen_ch7", "practice_generic_Chemistry", "What is the formula of rust?", "Fe2O3 . xH2O", "Fe3O4", "Fe(OH)3", "FeO", 0, "Rust is hydrated iron(III) oxide: Fe2O3 . xH2O."),
            QuestionEntity("sen_ch8", "practice_generic_Chemistry", "Which gas is released when sodium metal reacts with water?", "Hydrogen", "Oxygen", "Nitrogen", "Chlorine", 0, "Sodium reacts vigorously with water: 2Na + 2H2O -> 2NaOH + H2 (gas)."),
            QuestionEntity("sen_ch9", "practice_generic_Chemistry", "What is the hybridization of carbon in benzene?", "sp2", "sp3", "sp", "dsp2", 0, "Each carbon in benzene forms 3 sigma bonds (planar structure), signifying sp2 hybridization."),
            QuestionEntity("sen_ch10", "practice_generic_Chemistry", "Which of the following is an example of an emulsion?", "Milk", "Smoke", "Paint", "Cheese", 0, "Milk is a liquid-in-liquid colloidal system (emulsion) containing liquid fat dispersed in water."),
            QuestionEntity("sen_ch11", "practice_generic_Chemistry", "What is the units of rate constant for a zero order reaction?", "mol L^-1 s^-1", "s^-1", "L mol^-1 s^-1", "mol^2 L^-2 s^-1", 0, "For zero order, Rate = k. Thus units of rate constant are same as rate: mol L^-1 s^-1."),
            QuestionEntity("sen_ch12", "practice_generic_Chemistry", "Which ligand is bidentate?", "Oxalate ion", "Ammonia", "Water", "Cyanide ion", 0, "Oxalate (C2O4^2-) can donate two electron pairs from two oxygen atoms."),
            QuestionEntity("sen_ch13", "practice_generic_Chemistry", "Which of the following is the strongest acid?", "Trichloroacetic acid", "Acetic acid", "Monochloroacetic acid", "Dichloroacetic acid", 0, "Electron withdrawing chlorine atoms increase acidity. Three Cl atoms make trichloroacetic acid strongest."),
            QuestionEntity("sen_ch14", "practice_generic_Chemistry", "What is the principal ore of Aluminium?", "Bauxite", "Hematite", "Galena", "Cinnabar", 0, "Bauxite (Al2O3.2H2O) is the primary raw material for aluminium extraction."),
            QuestionEntity("sen_ch15", "practice_generic_Chemistry", "Which carbohydrate is stored in liver of animals?", "Glycogen", "Starch", "Cellulose", "Sucrose", 0, "Glycogen is the multi-branched polysaccharide of glucose that serves as energy storage in animals.")
        ),
        "Math" to listOf(
            QuestionEntity("sen_m1", "practice_generic_Math", "The integration of 2x dx is:", "x^2 + C", "2x^2 + C", "2 + C", "x + C", 0, "Integration of x^n dx is x^(n+1)/(n+1). So integral of 2x dx is 2 * (x^2/2) + C = x^2 + C."),
            QuestionEntity("sen_m2", "practice_generic_Math", "If log_e(x) = 1, then x is equal to:", "e", "1", "0", "10", 0, "By definition of logarithms, x = e^1 = e."),
            QuestionEntity("sen_m3", "practice_generic_Math", "What is the derivative of sin(x) with respect to x?", "cos(x)", "-cos(x)", "sin(x)", "-sin(x)", 0, "The standard derivative of sin(x) is cos(x)."),
            QuestionEntity("sen_m4", "practice_generic_Math", "If A is a matrix of order 2x3 and B is order 3x4, the order of matrix AB is:", "2x4", "3x3", "2x3", "4x2", 0, "Matrix multiplication order result is rows of first x columns of second = 2x4."),
            QuestionEntity("sen_m5", "practice_generic_Math", "The value of determinant |1 2; 3 4| is:", "-2", "2", "10", "-10", 0, "(1*4) - (2*3) = 4 - 6 = -2."),
            QuestionEntity("sen_m6", "practice_generic_Math", "What is the probability of getting a sum of 7 when two fair dice are rolled?", "6/36", "1/36", "12/36", "5/36", 0, "Favorable outcomes: (1,6), (2,5), (3,4), (4,3), (5,2), (6,1) = 6 out of 36."),
            QuestionEntity("sen_m7", "practice_generic_Math", "Find the direction cosines of a line parallel to z-axis.", "0, 0, 1", "1, 0, 0", "0, 1, 0", "1, 1, 1", 0, "A line parallel to z-axis makes angles 90, 90, 0 with axes, so direction cosines are cos(90), cos(90), cos(0) = 0, 0, 1."),
            QuestionEntity("sen_m8", "practice_generic_Math", "If vectors a and b are perpendicular, their dot product a.b is:", "0", "1", "-1", "ab", 0, "a.b = ab cos(theta). For perpendicular vectors, cos(90) = 0, so a.b = 0."),
            QuestionEntity("sen_m9", "practice_generic_Math", "What is the derivative of e^(3x) with respect to x?", "3 e^(3x)", "e^(3x)", "1/3 e^(3x)", "3x e^(3x-1)", 0, "Using chain rule, d/dx(e^(3x)) = e^(3x) * d/dx(3x) = 3 e^(3x)."),
            QuestionEntity("sen_m10", "practice_generic_Math", "Find the value of integral of 1/x dx from 1 to e.", "1", "e", "0", "log(e)", 0, "Integral of 1/x dx is log(x). Evaluated [log(e) - log(1)] = 1 - 0 = 1."),
            QuestionEntity("sen_m11", "practice_generic_Math", "What is the order of the differential equation d^2y/dx^2 + (dy/dx)^3 = 0?", "2", "3", "1", "0", 0, "The order is the highest derivative present in the equation, which is d^2y/dx^2 (Order 2)."),
            QuestionEntity("sen_m12", "practice_generic_Math", "Find the value of sin^-1(1/2) in principal branch.", "pi/6", "pi/3", "pi/4", "pi/2", 0, "sin(pi/6) = 1/2. Principal branch is [-pi/2, pi/2], so value is pi/6."),
            QuestionEntity("sen_m13", "practice_generic_Math", "If A is a square matrix of order 3 and |A| = 4, find |adj A|.", "16", "64", "4", "12", 0, "|adj A| = |A|^(n-1) = 4^(3-1) = 4^2 = 16."),
            QuestionEntity("sen_m14", "practice_generic_Math", "Find the slope of tangent to the curve y = x^2 at x = 3.", "6", "9", "3", "2", 0, "dy/dx = 2x. At x = 3, slope = 2(3) = 6."),
            QuestionEntity("sen_m15", "practice_generic_Math", "If events A and B are independent, P(A and B) is equal to:", "P(A) * P(B)", "P(A) + P(B)", "P(A) / P(B)", "P(A) - P(B)", 0, "By definition of independent events, P(A intersect B) = P(A) * P(B).")
        ),
        "Biology" to listOf(
            QuestionEntity("sen_bio1", "practice_generic_Biology", "Double fertilization is a characteristic feature of:", "Angiosperms", "Gymonosperms", "Pteridophytes", "Bryophytes", 0, "Double fertilization involves syngamy and triple fusion, unique to flowering plants (Angiosperms)."),
            QuestionEntity("sen_bio2", "practice_generic_Biology", "Which hormone is mainly responsible for ovulation in females?", "Luteinizing Hormone (LH)", "Progesterone", "Estrogen", "FSH", 0, "A surge in LH (LH surge) triggers the release of the ovum from the Graafian follicle."),
            QuestionEntity("sen_bio3", "practice_generic_Biology", "What is the genotypic ratio of a monohybrid cross in F2 generation?", "1:2:1", "3:1", "9:3:3:1", "1:1:1:1", 0, "Phenotypic ratio is 3:1, but genotypic ratio is 1 (Homozygous Dom): 2 (Heterozygous): 1 (Homozygous Rec)."),
            QuestionEntity("sen_bio4", "practice_generic_Biology", "DNA replication takes place in which phase of the cell cycle?", "S-phase", "G1-phase", "G2-phase", "M-phase", 0, "Synthesis phase (S-phase) is where DNA replication and centriole duplication occur."),
            QuestionEntity("sen_bio5", "practice_generic_Biology", "Which of the following is an auto-immune disease?", "Rheumatoid arthritis", "AIDS", "Cancer", "Allergy", 0, "Rheumatoid arthritis is a condition where the body attacks its own joint tissues."),
            QuestionEntity("sen_bio6", "practice_generic_Biology", "The relationship between mycorrhizae fungi and plant roots is:", "Mutualism", "Parasitism", "Commensalism", "Amensalism", 0, "Both benefit (fungus gets nutrients, plant gets mineral absorption assistance), signifying Mutualism."),
            QuestionEntity("sen_bio7", "practice_generic_Biology", "Which of the following is called the energy currency of the cell?", "ATP", "ADP", "DNA", "RNA", 0, "Adenosine Triphosphate (ATP) stores and transfers chemical energy inside cells."),
            QuestionEntity("sen_bio8", "practice_generic_Biology", "Which organism is called the connecting link between living and non-living?", "Virus", "Bacteria", "Euglena", "Chlamydomonas", 0, "Viruses are inert crystals outside hosts but reproduce inside living host cells."),
            QuestionEntity("sen_bio9", "practice_generic_Biology", "The coding segments of a split gene in eukaryotic DNA are called:", "Exons", "Introns", "Promoters", "Codons", 0, "Exons are expressed coding sequences, whereas introns are non-coding sequences spliced out during transcription processing."),
            QuestionEntity("sen_bio10", "practice_generic_Biology", "Which antibody is most abundant in human colostrum breast milk?", "IgA", "IgG", "IgM", "IgE", 0, "Immunoglobulin A (IgA) provides passive immunity to newborns through colostrum."),
            QuestionEntity("sen_bio11", "practice_generic_Biology", "Bt cotton is a transgenic plant resistant to:", "Insect pests", "Salt stress", "Drought", "Fungal diseases", 0, "Bt cotton contains Cry genes from Bacillus thuringiensis toxic to lepidopteran insect pests."),
            QuestionEntity("sen_bio12", "practice_generic_Biology", "What is the primary ecological role of decomposers in an ecosystem?", "Recycling of nutrients", "Fixing nitrogen", "Producing oxygen", "Consuming primary producers", 0, "Decomposers break down dead organic matter, returning inorganic minerals to the nutrient cycle."),
            QuestionEntity("sen_bio13", "practice_generic_Biology", "Which technique is used to amplify DNA segments in vitro?", "PCR (Polymerase Chain Reaction)", "Gel Electrophoresis", "Western Blotting", "Chromatography", 0, "PCR replicates DNA sequences exponentially using thermal cyclers."),
            QuestionEntity("sen_bio14", "practice_generic_Biology", "The process of formation of mRNA from DNA is called:", "Transcription", "Translation", "Replication", "Reverse Transcription", 0, "Transcription is RNA synthesis from a DNA template catalyzed by RNA polymerase."),
            QuestionEntity("sen_bio15", "practice_generic_Biology", "Which organ in human body is main site of lipid absorption?", "Small intestine", "Stomach", "Mouth", "Large intestine", 0, "Bile emulsifies fats, and small intestine cells absorb fatty acids/glycerol via lacteals.")
        ),
        "GK" to listOf(
            QuestionEntity("sen_gk1", "practice_generic_GK", "Which constitutional amendment is known as the 'Mini Constitution' of India?", "42nd Amendment", "44th Amendment", "52nd Amendment", "73rd Amendment", 0, "The 42nd Amendment Act of 1976 introduced massive changes to the Constitution."),
            QuestionEntity("sen_gk2", "practice_generic_GK", "The concept of 'Five Year Plans' in India was borrowed from which country?", "USSR", "USA", "UK", "France", 0, "Five Year Plans were borrowed from the Soviet Union (USSR)."),
            QuestionEntity("sen_gk3", "practice_generic_GK", "Where is the headquarters of the World Trade Organization (WTO) located?", "Geneva", "New York", "Paris", "London", 0, "The WTO was established in 1995 and is headquartered in Geneva, Switzerland."),
            QuestionEntity("sen_gk4", "practice_generic_GK", "Who was the first Indian woman to win an Olympic medal?", "Karnam Malleswari", "Saina Nehwal", "Mary Kom", "P.T. Usha", 0, "Karnam Malleswari won a bronze medal in weightlifting at the 2000 Sydney Olympics."),
            QuestionEntity("sen_gk5", "practice_generic_GK", "Which is the highest mountain peak located entirely within India?", "Kangchenjunga", "Nanda Devi", "K2", "Kamet", 0, "Kangchenjunga is the highest peak in India and the third highest in the world."),
            QuestionEntity("sen_gk6", "practice_generic_GK", "In which year did the Reserve Bank of India (RBI) start its operations?", "1935", "1947", "1950", "1921", 0, "RBI was established on April 1, 1935 under the Reserve Bank of India Act, 1934."),
            QuestionEntity("sen_gk7", "practice_generic_GK", "Who was the first President of independent India?", "Dr. Rajendra Prasad", "Dr. S. Radhakrishnan", "Zakir Husain", "Jawaharlal Nehru", 0, "Dr. Rajendra Prasad served as President from 1950 to 1962."),
            QuestionEntity("sen_gk8", "practice_generic_GK", "What is the currency of South Africa?", "Rand", "Shilling", "Dollar", "Pound", 0, "The South African Rand (ZAR) is the official currency."),
            QuestionEntity("sen_gk9", "practice_generic_GK", "Which is the largest gland in the human body?", "Liver", "Pancreas", "Thyroid", "Pituitary", 0, "The liver is the largest internal gland/organ in humans."),
            QuestionEntity("sen_gk10", "practice_generic_GK", "Who is the author of the book 'Discovery of India'?", "Jawaharlal Nehru", "Mahatma Gandhi", "Rabindranath Tagore", "Subhas Chandra Bose", 0, "Jawaharlal Nehru wrote it during his imprisonment in Ahmednagar Fort."),
            QuestionEntity("sen_gk11", "practice_generic_GK", "Which city is known as the Silicon Valley of India?", "Bengaluru", "Hyderabad", "Pune", "Mumbai", 0, "Bengaluru is the hub of information technology and startups in India."),
            QuestionEntity("sen_gk12", "practice_generic_GK", "In which year did India win its first Cricket World Cup?", "1983", "2011", "2007", "1999", 0, "Kapil Dev led India to victory against West Indies at Lord's in 1983."),
            QuestionEntity("sen_gk13", "practice_generic_GK", "Which gas is used in fire extinguishers to put out fires?", "Carbon dioxide", "Oxygen", "Nitrogen", "Helium", 0, "CO2 displaces oxygen, suffocating the fire safely."),
            QuestionEntity("sen_gk14", "practice_generic_GK", "Which country is called the Land of the Rising Sun?", "Japan", "Norway", "Finland", "China", 0, "Japan is situated to the far east, where sun appears to rise first in Asia."),
            QuestionEntity("sen_gk15", "practice_generic_GK", "What is the chemical formula of common salt?", "NaCl", "NaHCO3", "Na2CO3", "KCl", 0, "Sodium chloride (NaCl) is table/common salt.")
        ),
        "Sanskrit" to listOf(
            QuestionEntity("sen_sans1", "practice_generic_Sanskrit", "संस्कृतव्याकरणे कति माहेश्वरसूत्राणि सन्ति?", "14 (चतुर्दश)", "10 (दश)", "12 (द्वादश)", "16 (षोडश)", 0, "अष्टाध्यायीग्रन्थे चतुर्दश माहेश्वरसूत्राणि सन्ति।"),
            QuestionEntity("sen_sans2", "practice_generic_Sanskrit", "‘हितोपदेशः’ कस्य रचना अस्ति?", "नारायणपण्डितः", "विष्णुशर्मा", "कालिदासः", "बाणभट्टः", 0, "हितोपदेशस्य रचयिता नारायणपण्डितः वर्तते।"),
            QuestionEntity("sen_sans3", "practice_generic_Sanskrit", "‘पितरौ’ इत्यस्मिन् पदे कः समासः वर्तते?", "द्वन्द्वसमासः", "तत्पुरुषसमासः", "अव्ययीभावः", "बहुव्रीहिः", 0, "माता च पिता च = पितरौ (एकशेषद्वन्द्व समासः)।"),
            QuestionEntity("sen_sans4", "practice_generic_Sanskrit", "अभिज्ञानशाकुन्तलम् ग्रन्थस्य लेखकः कः?", "महाकविकालिदासः", "भवभूतिः", "भारविः", "हर्षदेवः", 0, "अभिज्ञानशाकुन्तलम् महाकविकालिदासस्य विश्वप्रसिद्धं नाटकं वर्तते।"),
            QuestionEntity("sen_sans5", "practice_generic_Sanskrit", "‘लिखेत्’ इति क्रियापदे कः लकारः अस्ति?", "विधिलिङ्लकारः", "लोट्लकारः", "लट्लकारः", "लृट्लकारः", 0, "लिखेत् इति पदे विधिलिङ् लकारः (प्रथमपुरुष एकवचन) अस्ति।"),
            QuestionEntity("sen_sans6", "practice_generic_Sanskrit", "‘उपनिषद्’ ग्रन्थानां मुख्यप्रतिपाद्यः विषयः कः?", "दर्शनम्", "कर्मकाण्डम्", "इतिहासः", "आयुर्वेदः", 0, "उपनिषदां मुख्यविषयः अध्यात्मज्ञानं ब्रह्मदर्शनं च अस्ति।"),
            QuestionEntity("sen_sans7", "practice_generic_Sanskrit", "‘कादम्बरी’ ग्रन्थस्य रचयिता कः अस्ति?", "बाणभट्टः", "दण्डी", "सुबन्धुः", "हर्षवर्धनः", 0, "कादम्बरी बाणभट्टेन विरचिता गद्यकाव्यरचना अस्ति।"),
            QuestionEntity("sen_sans8", "practice_generic_Sanskrit", "‘रामः गच्छति’ अत्र क्रियापदं किम्?", "गच्छति", "रामः", "गच्छ", "गन्तुम्", 0, "गच्छति इति क्रियापदं लट्लकारे अस्ति।"),
            QuestionEntity("sen_sans9", "practice_generic_Sanskrit", "‘मया’ इति पदं कस्याः विभक्त्याः अस्ति?", "तृतीया", "प्रथमा", "द्वितीया", "चतुर्थी", 0, "अस्मद् शब्दस्य तृतीया विभक्ति एकवचने मया रूपं भवति।"),
            QuestionEntity("sen_sans10", "practice_generic_Sanskrit", "‘पठ्’ धातोः लृट्लकारः प्रथमपुरुष एकवचनं किम्?", "पठिष्यति", "पठति", "पठिष्यसि", "पठतु", 0, "लृट्लकारे (भविष्यत् काले) पठिष्यति रूपं भवति।"),
            QuestionEntity("sen_sans11", "practice_generic_Sanskrit", "संस्कृतवाङ्मये कति पुराणाणि सन्ति?", "18 (अष्टादश)", "12 (द्वादश)", "24 (चतुर्विंशति)", "10 (दश)", 0, "संस्कृत साहित्ये अष्टादश महापुराणानि सन्ति।"),
            QuestionEntity("sen_sans12", "practice_generic_Sanskrit", "‘लघुसिद्धान्तकौमुदी’ ग्रन्थस्य रचयिता कः?", "वरदराजाचार्यः", "भट्टोजिदीक्षितः", "पाणिनिः", "पतञ्जलिः", 0, "लघुसिद्धान्तकौमुदी वरदराजाचार्येण विरचिता वर्तते।"),
            QuestionEntity("sen_sans13", "practice_generic_Sanskrit", "‘भय’ अर्थे का विभक्तिः प्रयुज्यते?", "पञ्चमी", "द्वितीया", "तृतीया", "षष्ठी", 0, "भयार्थानां प्रयोगे पञ्चमी विभक्तिः (भीत्रार्थानां भयहेतुः) प्रयुज्यते।"),
            QuestionEntity("sen_sans14", "practice_generic_Sanskrit", "‘सूर्यास्तः’ इत्यत्र कः सन्धिः वर्तते?", "दीर्घसन्धिः", "गुणसन्धिः", "वृद्धिसन्धिः", "यण्सन्धिः", 0, "सूर्य + अस्तः = सूर्यास्तः (अ + अ = आ) दीर्घ सन्धिः।"),
            QuestionEntity("sen_sans15", "practice_generic_Sanskrit", "‘भविष्यति’ इत्यत्र कः पुरुषः अस्ति?", "प्रथमपुरुषः", "मध्यमपुरुषः", "उत्तमपुरुषः", "अन्यपुरुषः", 0, "भविष्यति लृट्लकारस्य प्रथमपुरुष एकवचनं वर्तते।")
        ),
        "Geography" to listOf(
            QuestionEntity("sen_geo1", "practice_generic_Geography", "Which river forms the largest river basin in the world?", "Amazon River", "Nile River", "Mississippi River", "Yangtze River", 0, "The Amazon River in South America forms the largest drainage basin in the world."),
            QuestionEntity("sen_geo2", "practice_generic_Geography", "Which layer of the atmosphere contains the ozone layer?", "Stratosphere", "Troposphere", "Mesosphere", "Thermosphere", 0, "The ozone layer is located in the lower portion of the Stratosphere."),
            QuestionEntity("sen_geo3", "practice_generic_Geography", "What type of rocks are formed by the solidification of molten magma?", "Igneous Rocks", "Sedimentary Rocks", "Metamorphic Rocks", "None of these", 0, "Igneous rocks form from cooling lava or magma."),
            QuestionEntity("sen_geo4", "practice_generic_Geography", "Which state is the largest producer of tea in India?", "Assam", "West Bengal", "Tamil Nadu", "Kerala", 0, "Assam is India's leading producer of tea."),
            QuestionEntity("sen_geo5", "practice_generic_Geography", "The boundary line between India and China is called:", "McMahon Line", "Radcliffe Line", "Durand Line", "Line of Control", 0, "The McMahon Line is the effective boundary between Northeast India and Tibet (China)."),
            QuestionEntity("sen_geo6", "practice_generic_Geography", "Which country is the leading producer of copper in the world?", "Chile", "Peru", "China", "United States", 0, "Chile is the world's largest producer of copper by a wide margin."),
            QuestionEntity("sen_geo7", "practice_generic_Geography", "Which is the largest peninsula in the world?", "Arabian Peninsula", "Indian Peninsula", "Deccan Peninsula", "Indochina Peninsula", 0, "The Arabian Peninsula is the largest peninsula, covering over 3 million square kilometers."),
            QuestionEntity("sen_geo8", "practice_generic_Geography", "What is the percentage of nitrogen in the Earth's atmosphere?", "78%", "21%", "0.9%", "0.03%", 0, "Nitrogen gas makes up about 78% of the Earth's atmosphere by volume."),
            QuestionEntity("sen_geo9", "practice_generic_Geography", "Which latitude passes through the middle of India?", "Tropic of Cancer", "Equator", "Tropic of Capricorn", "Prime Meridian", 0, "The Tropic of Cancer (23.5 degrees N) passes through 8 states in India."),
            QuestionEntity("sen_geo10", "practice_generic_Geography", "Which is the highest plateau in the world?", "Tibetan Plateau", "Deccan Plateau", "Colorado Plateau", "Anatolian Plateau", 0, "The Tibetan Plateau is called the 'Roof of the World' and is the highest plateau."),
            QuestionEntity("sen_geo11", "practice_generic_Geography", "Which soil is best suited for the cultivation of cotton in India?", "Black Soil", "Alluvial Soil", "Red Soil", "Laterite Soil", 0, "Black soil (Regur soil) is highly clayey and moisture-retentive, ideal for cotton."),
            QuestionEntity("sen_geo12", "practice_generic_Geography", "Which is the deepest ocean on Earth?", "Pacific Ocean", "Atlantic Ocean", "Indian Ocean", "Southern Ocean", 0, "The Pacific Ocean contains the Mariana Trench, the deepest point on Earth."),
            QuestionEntity("sen_geo13", "practice_generic_Geography", "The standard meridian of India passes through which city?", "Mirzapur", "New Delhi", "Mumbai", "Kolkata", 0, "IST is calculated based on 82.5 degrees E longitude passing through Mirzapur, UP."),
            QuestionEntity("sen_geo14", "practice_generic_Geography", "Which of the following is a non-renewable resource?", "Coal", "Solar energy", "Wind energy", "Forests", 0, "Fossil fuels like coal take millions of years to form and are non-renewable."),
            QuestionEntity("sen_geo15", "practice_generic_Geography", "Which Indian state has the longest coastline?", "Gujarat", "Andhra Pradesh", "Tamil Nadu", "Maharashtra", 0, "Gujarat has the longest coastline in India, extending over 1,600 km.")
        ),
        "History" to listOf(
            QuestionEntity("sen_his1", "practice_generic_History", "In which year did the First Battle of Panipat take place?", "1526", "1556", "1761", "1504", 0, "Babur defeated Ibrahim Lodi in the First Battle of Panipat in 1526, establishing Mughal rule."),
            QuestionEntity("sen_his2", "practice_generic_History", "Who was the founder of the Maurya Empire?", "Chandragupta Maurya", "Ashoka the Great", "Bindusara", "Harsha", 0, "Chandragupta Maurya founded the Maurya Empire in 322 BCE with Chanakya's guidance."),
            QuestionEntity("sen_his3", "practice_generic_History", "The Indus Valley Civilization site 'Harappa' was excavated under the supervision of:", "Daya Ram Sahni", "R.D. Banerji", "John Marshall", "Alexander Cunningham", 0, "Daya Ram Sahni led the excavation of Harappa in 1921."),
            QuestionEntity("sen_his4", "practice_generic_History", "In which year did the Quit India Movement start?", "1942", "1930", "1920", "1947", 0, "Mahatma Gandhi launched the Quit India Movement in August 1942 at Bombay."),
            QuestionEntity("sen_his5", "practice_generic_History", "Who was the author of the book 'Indica'?", "Megasthenes", "Chanakya", "Fahien", "Hiuen Tsang", 0, "Megasthenes was a Greek ambassador in Chandragupta Maurya's court who wrote Indica."),
            QuestionEntity("sen_his6", "practice_generic_History", "The permanent settlement system of land revenue was introduced in Bengal by:", "Lord Cornwallis", "Warren Hastings", "Lord Wellesley", "Lord William Bentinck", 0, "Lord Cornwallis introduced the Permanent Settlement in 1793."),
            QuestionEntity("sen_his7", "practice_generic_History", "Who was the founder of the Gupta Dynasty?", "Sri Gupta", "Chandragupta I", "Samudragupta", "Ramagupta", 0, "Sri Gupta is historically regarded as the founder of the Gupta Empire (around 240 CE)."),
            QuestionEntity("sen_his8", "practice_generic_History", "Which Mughal emperor built the Red Fort in Delhi?", "Shah Jahan", "Akbar", "Jahangir", "Aurangzeb", 0, "Shah Jahan commissioned the building of the Red Fort in 1638 when capital shifted to Delhi."),
            QuestionEntity("sen_his9", "practice_generic_History", "The famous cell prison 'Cellular Jail' (Kala Pani) is located in:", "Port Blair", "Mumbai", "Chennai", "Kolkata", 0, "Cellular Jail in Port Blair, Andaman & Nicobar, housed political prisoners during British rule."),
            QuestionEntity("sen_his10", "practice_generic_History", "Who gave the slogan 'Give me blood and I will give you freedom'?", "Subhas Chandra Bose", "Bhagat Singh", "Mahatma Gandhi", "Bal Gangadhar Tilak", 0, "Netaji Bose gave this call during speech to Indian National Army."),
            QuestionEntity("sen_his11", "practice_generic_History", "In which year did the Jallianwala Bagh massacre happen?", "1919", "1920", "1915", "1930", 0, "General Dyer ordered troops to fire on gathering in Amritsar on 13 April 1919."),
            QuestionEntity("sen_his12", "practice_generic_History", "Who was the last Mughal emperor of India and ruler during the Revolt of 1857?", "Bahadur Shah Zafar", "Shah Alam II", "Akbar Shah II", "Bahadur Shah I", 0, "Bahadur Shah Zafar was the last Mughal emperor, who was declared the leader of the 1857 uprising by the sepoys."),
            QuestionEntity("sen_his13", "practice_generic_History", "Which Chinese traveler visited India during Harshavardhana's reign?", "Hiuen Tsang (Xuanzang)", "Fa-Hien", "I-Tsing", "Megasthenes", 0, "Hiuen Tsang spent several years in Harsha's court and Nalanda University."),
            QuestionEntity("sen_his14", "practice_generic_History", "Who was the last Governor-General of independent India?", "C. Rajagopalachari", "Lord Mountbatten", "Lord Canning", "Jawaharlal Nehru", 0, "C. Rajagopalachari (Rajaji) was the only Indian Governor-General."),
            QuestionEntity("sen_his15", "practice_generic_History", "The city of Vijayanagara (Hampi) was founded on the banks of which river?", "Tungabhadra", "Krishna", "Cauvery", "Godavari", 0, "Harihara and Bukka founded the empire on the banks of Tungabhadra in 1336.")
        ),
        "Political Science" to listOf(
            QuestionEntity("sen_pol1", "practice_generic_Political_Science", "Who acts as the ex-officio Chairman of Rajya Sabha in India?", "Vice-President of India", "President of India", "Prime Minister", "Speaker of Lok Sabha", 0, "The Vice-President of India is the ex-officio chairman of Rajya Sabha."),
            QuestionEntity("sen_pol2", "practice_generic_Political_Science", "The concept of 'Directive Principles of State Policy' (DPSP) is borrowed from:", "Ireland", "USA", "Canada", "Australia", 0, "DPSPs (Part IV) were borrowed from the Irish Constitution."),
            QuestionEntity("sen_pol3", "practice_generic_Political_Science", "Which article of the Indian Constitution empowers the President to declare national emergency?", "Article 352", "Article 356", "Article 360", "Article 368", 0, "Article 352 deals with National Emergency, Article 356 with State Emergency (President's Rule), and Article 360 with Financial Emergency."),
            QuestionEntity("sen_pol4", "practice_generic_Political_Science", "How many schedules are there in the Indian Constitution currently?", "12", "8", "10", "15", 0, "There are currently 12 schedules. Original constitution had 8."),
            QuestionEntity("sen_pol5", "practice_generic_Political_Science", "Who appoints the Chief Justice of India?", "President of India", "Prime Minister", "Law Minister", "Collegium of Supreme Court", 0, "The President of India appoints the CJI under Article 124(2)."),
            QuestionEntity("sen_pol6", "practice_generic_Political_Science", "Which fundamental right cannot be suspended even during a national emergency?", "Right to Life and Personal Liberty", "Right to Freedom of Speech", "Right to Equality", "Right to Freedom of Religion", 0, "Articles 20 and 21 (protection in respect of conviction and protection of life/liberty) cannot be suspended."),
            QuestionEntity("sen_pol7", "practice_generic_Political_Science", "What is the maximum strength of Lok Sabha as per Constitution?", "552", "545", "550", "250", 0, "Maximum strength is 552, though currently active representatives are fewer."),
            QuestionEntity("sen_pol8", "practice_generic_Political_Science", "Who is the custodian of the Indian Constitution?", "Supreme Court of India", "President of India", "Prime Minister", "Parliament", 0, "Supreme Court acts as interpreter and guardian of the Constitution."),
            QuestionEntity("sen_pol9", "practice_generic_Political_Science", "Which amendment introduced municipal corporations (Local Self Govt)?", "74th Amendment", "73rd Amendment", "42nd Amendment", "44th Amendment", 0, "74th Amendment Act of 1992 gave constitutional status to urban local bodies."),
            QuestionEntity("sen_pol10", "practice_generic_Political_Science", "The design of democratic decentralization in India is based on which committee?", "Balwant Rai Mehta Committee", "Sarkaria Commission", "Kothari Commission", "Swaran Singh Committee", 0, "Balwant Rai Mehta Committee in 1957 recommended 3-tier Panchayati Raj system."),
            QuestionEntity("sen_pol11", "practice_generic_Political_Science", "Who was the first woman Speaker of Lok Sabha?", "Meira Kumar", "Sumitra Mahajan", "Pratibha Patil", "Sarojini Naidu", 0, "Meira Kumar served as Lok Sabha speaker from 2009 to 2014."),
            QuestionEntity("sen_pol12", "practice_generic_Political_Science", "The concept of Judicial Review in India is borrowed from which country?", "United States", "United Kingdom", "Canada", "Ireland", 0, "Judicial Review concept is adapted from American constitutional practices."),
            QuestionEntity("sen_pol13", "practice_generic_Political_Science", "In which case did Supreme Court formulate the 'Basic Structure' doctrine?", "Kesavananda Bharati case", "Golaknath case", "Minerva Mills case", "Maneka Gandhi case", 0, "Supreme Court ruled in 1973 that Parliament cannot alter basic structure of Constitution."),
            QuestionEntity("sen_pol14", "practice_generic_Political_Science", "Who administers the oath of office to the President of India?", "Chief Justice of India", "Vice-President", "Prime Minister", "Speaker of Lok Sabha", 0, "Chief Justice of India administers oath under Article 60."),
            QuestionEntity("sen_pol15", "practice_generic_Political_Science", "How many languages are recognized in the 8th Schedule of Constitution?", "22", "18", "14", "20", 0, "There are currently 22 languages in the 8th schedule.")
        ),
        "Economics" to listOf(
            QuestionEntity("sen_eco1", "practice_generic_Economics", "The law of demand states that other things remaining constant, as price increases:", "Demand decreases", "Demand increases", "Supply decreases", "No change", 0, "The law of demand establishes an inverse relationship between price and quantity demanded."),
            QuestionEntity("sen_eco2", "practice_generic_Economics", "Inflation is measured in India primarily using which index?", "Consumer Price Index (CPI)", "Wholesale Price Index (WPI)", "GNP Deflator", "Sensex", 0, "RBI uses CPI (Combined) as the key metric to track retail inflation."),
            QuestionEntity("sen_eco3", "practice_generic_Economics", "What does 'Laissez-Faire' economics advocate?", "Minimal government intervention", "Complete government control", "Heavy subsidies", "Agricultural taxes", 0, "Laissez-faire translates to 'let do', advocating free markets with minimal regulation."),
            QuestionEntity("sen_eco4", "practice_generic_Economics", "Which sector contributes the highest share to India's Gross Value Added (GVA)?", "Services Sector", "Agriculture Sector", "Manufacturing Sector", "Mining Sector", 0, "The service sector contributes over 53% of India's GVA."),
            QuestionEntity("sen_eco5", "practice_generic_Economics", "Who is regarded as the Father of Modern Economics?", "Adam Smith", "John Maynard Keynes", "Karl Marx", "Alfred Marshall", 0, "Adam Smith wrote 'The Wealth of Nations' in 1776, establishing modern economic science."),
            QuestionEntity("sen_eco6", "practice_generic_Economics", "What type of unemployment is characterized by people being out of work between jobs?", "Frictional unemployment", "Structural unemployment", "Seasonal unemployment", "Cyclical unemployment", 0, "Frictional unemployment represents the temporary search time when moving between jobs."),
            QuestionEntity("sen_eco7", "practice_generic_Economics", "What does GDP stand for?", "Gross Domestic Product", "Gross Development Product", "General Domestic Product", "Government Debt Percentage", 0, "GDP is the total market value of final goods/services produced in a country in a year."),
            QuestionEntity("sen_eco8", "practice_generic_Economics", "Who regulates the monetary policy in India?", "Reserve Bank of India (RBI)", "Ministry of Finance", "SEBI", "NITI Aayog", 0, "RBI controls interest rates and money supply to manage inflation and growth."),
            QuestionEntity("sen_eco9", "practice_generic_Economics", "When a tax is levied on income, it is called a/an:", "Direct Tax", "Indirect Tax", "Value Added Tax", "GST", 0, "Direct taxes are paid directly by individuals/entities to the government."),
            QuestionEntity("sen_eco10", "practice_generic_Economics", "Which entity replaced the Planning Commission of India in 2015?", "NITI Aayog", "Finance Commission", "National Development Council", "GST Council", 0, "NITI Aayog was formed on January 1, 2015 as a policy think tank."),
            QuestionEntity("sen_eco11", "practice_generic_Economics", "What is a 'Budget Deficit'?", "Expenses exceed revenues", "Revenues exceed expenses", "Balance of trade is negative", "Tax collection decreases", 0, "Deficit occurs when government spending exceeds its revenue receipts."),
            QuestionEntity("sen_eco12", "practice_generic_Economics", "The 'Gini Coefficient' is used to measure:", "Income inequality", "Inflation rate", "Unemployment rate", "Poverty index", 0, "Gini coefficient measures dispersion of wealth/income in a population."),
            QuestionEntity("sen_eco13", "practice_generic_Economics", "Which curve shows relationship between tax rates and tax revenue?", "Laffer Curve", "Phillips Curve", "Lorenz Curve", "Kuznets Curve", 0, "Laffer curve shows tax revenues first rising then falling as tax rate increases."),
            QuestionEntity("sen_eco14", "practice_generic_Economics", "Who proposed the 'General Theory of Employment, Interest, and Money'?", "John Maynard Keynes", "Milton Friedman", "David Ricardo", "Karl Marx", 0, "Keynes published this in 1936, founding modern macroeconomics."),
            QuestionEntity("sen_eco15", "practice_generic_Economics", "What does 'FDI' stand for?", "Foreign Direct Investment", "Foreign Debt Index", "Federal Deposit Institution", "Financial Development Integration", 0, "FDI is investment made by a firm/individual in one country into business interests in another.")
        ),
        "Accountancy" to listOf(
            QuestionEntity("sen_acc1", "practice_generic_Accountancy", "The process of recording financial transactions in chronological order is:", "Journalizing", "Posting", "Summarizing", "Analyzing", 0, "Journalizing is entering transactions in the journal in date order."),
            QuestionEntity("sen_acc2", "practice_generic_Accountancy", "Which of the following is a representative personal account?", "Outstanding Salary Account", "Cash Account", "Machinery Account", "Rent Account", 0, "Outstanding Salary represents employees to whom salary is owed, making it representative personal."),
            QuestionEntity("sen_acc3", "practice_generic_Accountancy", "The goodwill of a business is classified as a/an:", "Intangible Asset", "Tangible Asset", "Current Asset", "Fictitious Asset", 0, "Goodwill has no physical form but represents valuable reputational asset, hence intangible."),
            QuestionEntity("sen_acc4", "practice_generic_Accountancy", "Which accounting concept dictates that personal transactions of owner should not mix with business?", "Business Entity Concept", "Going Concern Concept", "Money Measurement Concept", "Dual Aspect Concept", 0, "The Business Entity concept states that a business is separate from its owners."),
            QuestionEntity("sen_acc5", "practice_generic_Accountancy", "Trial Balance is prepared to check the:", "Arithmetical accuracy of ledger", "Financial position", "Profit or loss", "Cash balance", 0, "A trial balance checks if total debits equal total credits arithmetically."),
            QuestionEntity("sen_acc6", "practice_generic_Accountancy", "The decrease in the value of fixed assets due to wear and tear is called:", "Depreciation", "Appreciation", "Amortization", "Depletion", 0, "Depreciation is the systematic allocation of cost of tangible fixed assets over their useful life."),
            QuestionEntity("sen_acc7", "practice_generic_Accountancy", "Which of the following is a liability?", "Bank Overdraft", "Debtors", "Prepaid Insurance", "Patents", 0, "Bank overdraft is a short-term bank debt, hence a current liability."),
            QuestionEntity("sen_acc8", "practice_generic_Accountancy", "What is the basic accounting equation?", "Assets = Liabilities + Capital", "Assets = Liabilities - Capital", "Liabilities = Assets + Capital", "Capital = Assets + Liabilities", 0, "Basic accounting balance sheet equation is Assets = Liabilities + Owner's Equity (Capital)."),
            QuestionEntity("sen_acc9", "practice_generic_Accountancy", "What type of account is Depreciation Account?", "Nominal Account", "Real Account", "Personal Account", "Valuation Account", 0, "Depreciation is an expense/loss, so it is classified as a nominal account."),
            QuestionEntity("sen_acc10", "practice_generic_Accountancy", "Cash discount is allowed on:", "Prompt payment", "Sales promotion", "Bulk purchase", "Trade contract", 0, "Cash discounts encourage buyers to pay their bills quickly."),
            QuestionEntity("sen_acc11", "practice_generic_Accountancy", "Which book records all cash receipts and cash payments?", "Cash Book", "Sales Journal", "Purchase Journal", "General Ledger", 0, "Cash Book is a book of original entry recording all cash/bank transactions."),
            QuestionEntity("sen_acc12", "practice_generic_Accountancy", "Prepaid Rent is classified as a/an:", "Asset", "Expense", "Liability", "Income", 0, "Prepaid rent is rent paid in advance, representing future economic benefit (Current Asset)."),
            QuestionEntity("sen_acc13", "practice_generic_Accountancy", "What is the main objective of preparing a Balance Sheet?", "To show financial position", "To calculate net profit", "To verify cash balances", "To record daily sales", 0, "Balance sheet presents assets, liabilities, and capital at a specific date."),
            QuestionEntity("sen_acc14", "practice_generic_Accountancy", "Double entry system of book-keeping was introduced by:", "Luca Pacioli", "F.W. Taylor", "Adam Smith", "Henry Fayol", 0, "Luca Pacioli published the first description of double-entry system in 1494."),
            QuestionEntity("sen_acc15", "practice_generic_Accountancy", "The closing stock is valued at:", "Cost price or Market price whichever is lower", "Cost price", "Market price", "Average price", 0, "According to conservatism principle, inventory is valued at lower of cost or net realizable value.")
        ),
        "Business Studies" to listOf(
            QuestionEntity("sen_bst1", "practice_generic_Business_Studies", "Which of the following is NOT a feature of management?", "Single-dimensional", "Goal-oriented", "Continuous process", "Group activity", 0, "Management is multi-dimensional (managing work, people, and operations)."),
            QuestionEntity("sen_bst2", "practice_generic_Business_Studies", "Who is regarded as the Father of Scientific Management?", "F.W. Taylor", "Henri Fayol", "Max Weber", "Peter Drucker", 0, "Frederick Winslow Taylor pioneered scientific management principles."),
            QuestionEntity("sen_bst3", "practice_generic_Business_Studies", "The process of grouping similar activities together is part of which function of management?", "Organizing", "Planning", "Staffing", "Directing", 0, "Organizing involves division of work, grouping jobs into departments, and assigning duties."),
            QuestionEntity("sen_bst4", "practice_generic_Business_Studies", "Under which style of leadership does the leader make all decisions alone?", "Autocratic Leadership", "Democratic Leadership", "Laissez-Faire Leadership", "Free-rein Leadership", 0, "An autocratic leader centralizes authority and gives orders without consulting others."),
            QuestionEntity("sen_bst5", "practice_generic_Business_Studies", "Which of the following is a internal source of recruitment?", "Promotion", "Advertisement", "Campus Placement", "Employment Exchange", 0, "Promotion fills vacancies from existing staff inside the business."),
            QuestionEntity("sen_bst6", "practice_generic_Business_Studies", "The apex level of planning in a company is performed by:", "Top Level Management", "Middle Level Management", "Supervisory Management", "Operational Staff", 0, "Top-level managers (Board, CEO, GM) formulate long-term goals and strategic plans."),
            QuestionEntity("sen_bst7", "practice_generic_Business_Studies", "Who defined 14 principles of general management?", "Henri Fayol", "F.W. Taylor", "Max Weber", "Harold Koontz", 0, "Henri Fayol outlined the 14 principles (Division of Work, Authority, etc.) in his book."),
            QuestionEntity("sen_bst8", "practice_generic_Business_Studies", "Which management function is concerned with monitoring performance?", "Controlling", "Planning", "Organizing", "Staffing", 0, "Controlling compares actual progress against standards and corrects deviations."),
            QuestionEntity("sen_bst9", "practice_generic_Business_Studies", "What is the primary motive of a business enterprise?", "Earning profit", "Social service", "Government taxation", "Employee benefit", 0, "While social duties exist, profit earning is the primary survival/growth motive."),
            QuestionEntity("sen_bst10", "practice_generic_Business_Studies", "A partnership firm must have a minimum of how many partners?", "2", "5", "10", "20", 0, "At least two persons must agree to form a partnership."),
            QuestionEntity("sen_bst11", "practice_generic_Business_Studies", "Which of the following is a component of economic environment?", "Interest rates", "Family structure", "Literacy rates", "Technological systems", 0, "Economic factors like interest rates, inflation, and GDP form the economic environment."),
            QuestionEntity("sen_bst12", "practice_generic_Business_Studies", "The process of delegating authority downwards is called:", "Decentralization", "Departmentalization", "Centralization", "Coordination", 0, "Decentralization is the systematic dispersal of decision-making authority throughout levels."),
            QuestionEntity("sen_bst13", "practice_generic_Business_Studies", "Which type of plan is a statement of expected results in numerical terms?", "Budget", "Rule", "Method", "Policy", 0, "A budget quantifies future operations and expected results (e.g. Sales budget)."),
            QuestionEntity("sen_bst14", "practice_generic_Business_Studies", "The right of a manager to give orders and extract obedience is called:", "Authority", "Responsibility", "Accountability", "Delegation", 0, "Fayol defined authority as the formal right to command subordinates."),
            QuestionEntity("sen_bst15", "practice_generic_Business_Studies", "Which barrier to communication arises from emotional or mental state?", "Psychological barrier", "Semantic barrier", "Organizational barrier", "Personal barrier", 0, "Emotions like anger or distrust create psychological blocks to understanding.")
        )
    )

    fun getQuestions(className: String, subject: String, count: Int = 5, seenIds: Set<String> = emptySet()): List<QuestionEntity> {
        val key = "${className}_${subject}"
        val list = questions[key]
            ?: questions.entries.firstOrNull { it.key.equals(key, ignoreCase = true) }?.value
            ?: run {
            val numberPart = className.filter { it.isDigit() }.toIntOrNull()
            val normalizedSubject = subject.trim()
            if (numberPart != null) {
                if (numberPart in 1..5) {
                    primarySubjectQuestions[normalizedSubject]
                        ?: primarySubjectQuestions.entries.firstOrNull { it.key.equals(normalizedSubject, ignoreCase = true) }?.value
                        ?: primarySubjectQuestions["Math"]
                        ?: primarySubjectQuestions["GK"]
                        ?: emptyList()
                } else if (numberPart in 6..9) {
                    secondarySubjectQuestions[normalizedSubject]
                        ?: secondarySubjectQuestions.entries.firstOrNull { it.key.equals(normalizedSubject, ignoreCase = true) }?.value
                        ?: secondarySubjectQuestions[normalizedSubject.replace("Grammar", "Grammer", ignoreCase = true)]
                        ?: secondarySubjectQuestions[normalizedSubject.replace("Grammer", "Grammar", ignoreCase = true)]
                        ?: secondarySubjectQuestions["Science"]
                        ?: secondarySubjectQuestions["GK"]
                        ?: emptyList()
                } else {
                    // Class 10, 11, 12
                    seniorSubjectQuestions[normalizedSubject]
                        ?: seniorSubjectQuestions.entries.firstOrNull { it.key.equals(normalizedSubject, ignoreCase = true) }?.value
                        ?: seniorSubjectQuestions["English"]
                        ?: emptyList()
                }
            } else {
                if (className.contains("JEE", ignoreCase = true) || className.contains("NEET", ignoreCase = true)) {
                    seniorSubjectQuestions[normalizedSubject] ?: seniorSubjectQuestions["Physics"] ?: seniorSubjectQuestions["English"] ?: emptyList()
                } else {
                    secondarySubjectQuestions[normalizedSubject] ?: secondarySubjectQuestions["Science"] ?: secondarySubjectQuestions["GK"] ?: emptyList()
                }
            }
        }
        
        val unseenList = list.filter { it.id !in seenIds }
        val practiceList = if (unseenList.size >= count) {
            unseenList.shuffled().take(count)
        } else {
            val seenList = list.filter { it.id in seenIds }.shuffled()
            (unseenList.shuffled() + seenList).take(count)
        }
        
        val safeList = if (practiceList.isNotEmpty()) {
            practiceList
        } else {
            getDefaultPracticeQuestions(className, subject).take(count)
        }
        
        return safeList.map { q ->
            val originalOptions = listOf(q.optionA, q.optionB, q.optionC, q.optionD)
            val correctOptionText = originalOptions.getOrElse(q.correctAnswer) { q.optionA }
            
            // Randomly shuffle options dynamically to avoid static Option A bias
            val shuffledOptions = originalOptions.shuffled()
            val newCorrectIndex = shuffledOptions.indexOf(correctOptionText)
            
            q.copy(
                quizId = "practice_${className}_${subject}",
                optionA = shuffledOptions.getOrElse(0) { "" },
                optionB = shuffledOptions.getOrElse(1) { "" },
                optionC = shuffledOptions.getOrElse(2) { "" },
                optionD = shuffledOptions.getOrElse(3) { "" },
                correctAnswer = if (newCorrectIndex != -1) newCorrectIndex else 0
            )
        }
    }

    private fun getDefaultPracticeQuestions(className: String, subject: String): List<QuestionEntity> {
        return listOf(
            QuestionEntity("def_01", "practice_${className}_${subject}", "What is the speed of light in a vacuum?", "3 x 10^8 m/s", "3 x 10^5 m/s", "1.5 x 10^8 m/s", "3 x 10^6 m/s", 0, "Light travels at approximately 300,000 km/s (3 x 10^8 m/s) in vacuum."),
            QuestionEntity("def_02", "practice_${className}_${subject}", "Which gas is absorbed by green plants during photosynthesis?", "Carbon Dioxide", "Oxygen", "Nitrogen", "Hydrogen", 0, "Plants absorb carbon dioxide (CO2) from the air to perform photosynthesis."),
            QuestionEntity("def_03", "practice_${className}_${subject}", "What is the smallest composite number?", "4", "2", "1", "6", 0, "4 is the smallest composite number because its factors are 1, 2, and 4."),
            QuestionEntity("def_04", "practice_${className}_${subject}", "Which is the longest river in India?", "Ganga", "Yamuna", "Godavari", "Brahmaputra", 0, "The Ganga is the longest river running entirely within India at approximately 2,525 km."),
            QuestionEntity("def_05", "practice_${className}_${subject}", "The SI unit of force is:", "Newton", "Joule", "Pascal", "Watt", 0, "Force is measured in Newtons (N), named after Sir Isaac Newton.")
        )
    }
}

