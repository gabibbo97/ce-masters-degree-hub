%% Cleanup
clear % Clear variables
clc % Clean console
close all % Close all plots

%% Original function (ground truth)
num_true = 10000;
x_true = linspace(0,1,num_true)'; % Generation of the X axis
y_true = groundTruth(x_true);

figure
hold on
grid on

tiledlayout(3,3) % Arrange plots in a grid

%% Sampling from ground truth
num_samples = 10; % Number of points to sample from the function
sigma = 0.05; % Noise strength
x_sampling = rand(num_samples, 1); % Take num_samples points on the x-axis
y_sampling = sampleWithGaussianNoise(x_sampling, sigma); % Sample and add gaussian noise

%% Learning phase
lambdas = logspace(-3,1, 9);
lambdas(2) = lambdas(1);
lambdas(1) = 0; % Test lambda = 0
for lambda = lambdas % Iterate over lambdas
    p = num_samples - 1; % Use maximum p
    X_learning = zeros(num_samples, p+1);
    for i = 0:p
        X_learning(:,i + 1) = x_sampling .^ i;
    end
    c = (X_learning' * X_learning + lambda * eye(p+1)) \ (X_learning'*y_sampling);
    
    % Forward
    X_forward = zeros(num_true, p+1);
    for i = 0:p
        X_forward(:,i + 1) = x_true .^ i;
    end
    Y_forward = X_forward * c;
    
    average_error = mean(abs(Y_forward - y_true));
    
    Y_samples = X_learning * c;
    average_error_on_samples = mean(abs(Y_samples - y_sampling));

    nexttile;
    plot(x_sampling, y_sampling, 'ro', x_true, y_true, 'b', x_true, Y_forward, 'g-');
    axis([min(x_true), max(x_true), min(y_true), max(y_true)]) % Scale axis to ground truth function
    title(sprintf('lambda = %.2d\nmean error: %.2e\nmean error on samples: %.2e', lambda, average_error, average_error_on_samples));
    legend(sprintf('Samplings (%d)', num_samples), 'Ground truth', 'Prediction');
end

%% Definitions
function y = groundTruth(x)
    y = x.^2;
end
function y = sampleWithGaussianNoise(x, sigma)
    y = groundTruth(x) + sigma * randn(size(x));
end