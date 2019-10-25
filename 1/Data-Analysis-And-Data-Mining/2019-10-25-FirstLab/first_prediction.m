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
n = 5;
x = rand(n, 1); % n rows 1 column
y = x .^2 + sigma * randn(size(x)); % y + some gaussian noise

plot(x,y,'ob'); % Blue circles

%% Learning
p = 2;

mX = zeros(n, p+1);  
for j = 1:p+1
    mX(:,j) = x .^ (j-1);
end

vY = y;
vC = (mX' * mX)\mX'* vY;

%% Forward
% Plot the estimator
fX = zeros(nt, p+1);  
for j = 1:p+1
    fX(:,j) = xt .^ (j-1);
end
fY = fX * vC;

plot (xt, fY,'r');
