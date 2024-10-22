10 LET V_sum = 0
20 V_sum = F_addition(1, 1, 3)
30 PRINT V_sum
40
50 SUB F_addition(V_x, V_y, V_z)
60	 LOCAL V_result1, V_result2, V_result3
70	 IF V_z = 1 THEN GOTO 80 ELSE GOTO 110
80		 V_result1 = V_x + (V_y + V_z)
90		 RETURN V_result1
100	 GOTO 140
110		 V_result2 = V_z - 1
120		 V_result1 = F_addition(V_x, V_y, V_result2)
130		 RETURN V_result1
140	 GOTO 150
150	 ENDIF
160 END SUB
170 END
