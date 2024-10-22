10 LET V_sum = 0
20 V_sum = F_addition(1, 2, 3)
30 PRINT V_sum
40
50 SUB F_addition(V_x, V_y, V_z)
60	 LOCAL V_result1, V_result2, V_result3
70	 V_result1 = V_x + (V_y + V_z)
80	 RETURN V_result1
90 END SUB
100 END
