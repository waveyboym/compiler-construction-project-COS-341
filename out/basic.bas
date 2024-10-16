10 LET V_sum = 0
20 V_sum = F_addition(2, 2, 3)
30 PRINT V_sum
40
50 SUB F_addition(V_x, V_y, V_z)
60	 LOCAL V_result1, V_result2, V_result3
70	 IF (V_z * 1) = 3 THEN GOTO 80 ELSE GOTO 100
80		 V_result1 = V_x + (V_y + V_z)
90	 GOTO 110
100		 V_result1 = V_x * (V_y + V_z)
110	 GOTO 120
120	 ENDIF
130	 RETURN V_result1
140 END SUB
150 END
