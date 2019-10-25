%% Init
clear
close all
clc

%% Problem
% Generate the X
nt = 1000;
xt = linspace(0,1,nt)';

% Generate the y
yt = xt .^ 2;

% Plot
figure, hold on, box on, grid on;
plot(xt, yt, 'b');

%% Sampling
sigma = 0.1; % Noise dampening
n = 30;
x = rand(n, 1); % n rows 1 column
y = x .^2 + sigma * randn(size(x)); % y + some gaussian noise

plot(x,y,'ob'); % Blue circles

%% Learning
maxp = n - 1;
mX = zeros(n, maxp+1);  
for j = 1:maxp+1
    mX(:,j) = x .^ (j-1);
end
fX = zeros(nt, maxp+1);  
for j = 1:maxp+1
    fX(:,j) = xt .^ (j-1);
end

for p = 0:maxp
    c = (mX(:,1:p+1)' * mX(:,1:p+1))\mX(:,1:p+1)'* y;
    
    fY = fX(:,1:p+1) * c;
    err_oracle = mean(abs(yt - fY));
    
    fY = mX(:,1:p+1) * c;
    err_training_set = mean(abs(y - fY));
    
    fprintf("%d ERR(oracle): %.2e ERR(training set): %.2e\n", p, err_oracle, err_training_set);
end