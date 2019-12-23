import numpy as np

r = np.random.rand(12, 54) * 2.0 - 1.0
np.savetxt('weight_rand1.csv', r, fmt="%.4f", delimiter=',')
