%% Sampling
sigma = 0.1; % Noise dampening
n = 100;
x = rand(n, 1); % n rows 1 column
y = sin(6*x) + sigma * rand(size(x)); % y + some gaussian noise

plot(x,y,'ob'); % Blue circles