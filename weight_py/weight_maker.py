import numpy as np

r1 = np.random.rand(60, 16) * 2.0 - 1.0
r2 = np.random.rand(16, 10) * 2.0 - 1.0
r3 = np.random.rand(10, 32) * 2.0 - 1.0

with open('weight_rand.csv', 'w') as f:
  np.savetxt(f, r1, fmt="%.4f", delimiter=',')
  np.savetxt(f, r2, fmt="%.4f", delimiter=',')
  np.savetxt(f, r3, fmt="%.4f", delimiter=',')
