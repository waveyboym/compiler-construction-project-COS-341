10 LET V_globalVar = 0
20 V_globalVar = 3
30 IF (V_globalVar * 1) = 3 THEN GOTO 40 ELSE GOTO 80
40	 PRINT "First"
50	 PRINT "Plus"
60	 PRINT "First"
70 GOTO 110
80	 PRINT "Second"
90	 PRINT "Plus"
100	 PRINT "Second"
110 GOTO 120
120 ENDIF
130 END