10 LET V_count = 0
20 LET V_message$ = ""
30 INPUT V_count
40 V_message$ = "Welcome"
50 PRINT V_message$
60 IF V_count = 0 THEN GOTO 70 ELSE GOTO 90
70	 END
80 GOTO 100
90	 F_increment(V_count, V_count, V_count)
100 GOTO 110
110 ENDIF
120
130 SUB F_increment(V_x, V_y, V_z)
140	 LOCAL V_result, V_temp, V_dummy
150	 V_result = V_x + 1
160	 PRINT V_result
170	 IF V_result > 5 THEN GOTO 180 ELSE GOTO 200
180		 END
190	 GOTO 210
200		 F_increment(V_result, V_result, V_result)
210	 GOTO 220
220	 ENDIF
230 END SUB
240 END
