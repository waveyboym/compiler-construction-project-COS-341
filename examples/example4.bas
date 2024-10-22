10 LET V_sum = 0
20 V_sum = F_addition(2, 2, 3)
30 PRINT V_sum
40
50 SUB F_addition(V_x, V_y, V_z)
60	 LOCAL V_result1, V_result2, V_result3
70	 IF (V_z * 1) = 3 THEN GOTO 80 ELSE GOTO 130
80		 V_result2 = F_one(0, 0, 0)
90		 V_result3 = V_x + V_y
100		 V_result1 = V_result2 + V_result3
110		 RETURN V_result1
120	 GOTO 170
130		 V_result2 = F_one(0, 0, 0)
140		 V_result3 = V_x + V_z
150		 V_result1 = V_result2 + V_result3
160		 RETURN V_result1
170	 GOTO 180
180	 ENDIF
190 END SUB
200
210 SUB F_one(V_x1, V_y1, V_z1)
220	 LOCAL V_result11, V_result21, V_result31
230	 RETURN 1
240 END SUB
250 END