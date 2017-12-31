# ╥─────────────────────────────╥
# ║ IUS COPIANDI MMXVII Vexatos ║
# ║ NON CURO QUOD HAC RE FACIS. ║
# ╨─────────────────────────────╨
TUPLE_DIGITS = ["single", "double", "triple", "quadruple", "quintuple", "sextuple", "septuple", "octuple", "nonuple"]
TUPLE_DIGIT_PREFIXES = ["", "un", "duo", "tre", "quattuor", "quin", "sex", "septen", "duode", "unde"]
TUPLE_TENS = ["decuple", "vigintuple", "trigintuple", "quadragintuple", "quinquagintuple", "sexagintuple", "septuagintuple", "octogintuple", "nonagintuple"]
TUPLE_TENS_PREFIXES = ["", "deca", "viginta", "triginta", "quadraginta", "quinquaginta", "sexaginta", "septuaginta", "octoginta", "nonaginta", ""]
TUPLE_HUNDREDS = ["centuple", "ducentuple", "trecentuple", "quadringentuple", "quingentuple", "sescentuple", "septigentuple", "octigentuple", "nongentuple"]

for i in xrange(1,1000+1):
        s = ""
        if i >= 1 and i <= 9:
                s = TUPLE_DIGITS[i - 1]
        elif i >= 10 and i <= 97:
                tens = i / 10 if i % 10 >= 8 else (i / 10) - 1
                s = TUPLE_DIGIT_PREFIXES[i % 10] + TUPLE_TENS[tens]
        elif i >= 98 and i <= 997:
                tens = ((i % 100) / 10) + 1 if i % 10 >= 8 else (i % 100) / 10
                hundreds =  i / 100 if i % 100 >= 98 else (i / 100) - 1
                s = TUPLE_DIGIT_PREFIXES[i % 10] + TUPLE_TENS_PREFIXES[tens] + TUPLE_HUNDREDS[hundreds]
        elif i >= 998:
                s = TUPLE_DIGIT_PREFIXES[i % 10] + "milluple"
        print("preston.tuple.%d=%s" % (i, s.capitalize()))
