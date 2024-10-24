10 LET V_num1 = 0
20 LET V_num2 = 0
30 INPUT V_num1
40 V_num2 = V_num1 + 3
50 PRINT V_num2
60 IF V_num2 > 10 THEN GOTO 70 ELSE GOTO 90
70	 END
80 GOTO 100
90	 F_process(V_num1, V_num2, V_num2)
100 GOTO 110
110 ENDIF
120
130 SUB F_process(V_a, V_b, V_c)
140	 LOCAL V_res1, V_res2, V_res3
150	 V_res1 = V_a * V_b
160	 V_res2 = V_res1 - 5
170	 V_res3 = V_res2 / V_b
180	 PRINT V_res3
190	 IF V_res3 = 0 THEN GOTO 200 ELSE GOTO 220
200		 END
210	 GOTO 230
220		 F_process(V_res3, V_b, V_c)
230	 GOTO 240
240	 ENDIF
250 END SUB
260 END
