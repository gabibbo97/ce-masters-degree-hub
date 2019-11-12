%% Cleanup
clear % Clear variables
clc % Clean console
close all % Close all plots

%% Original function (ground truth)
num_true = 10000;
x_true = linspace(0,1,num_true); % Generation of the X axis
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

%% Learning from sample data
% We now build a function like sum over i {c * x^i}
p = num_samples - 1; % Degree of the approximating polynomial (should be between 1 and num_samples - 1)
learn_X = zeros(num_samples, p+1); % Matrix initialization
for i = 1:p+1 % The logical choice here would be 0 to p but MATLAB doesn't like that
    learn_X(:,i) = x_sampling .^ (i - 1); % Calculation of x^i for all xs
end
for current_p = 1:p
    % Learning phase
    learn_X_current = learn_X(:,1:current_p+1); % Rescale matrix
    learn_C_current = (learn_X_current' * learn_X_current) \ (learn_X_current' * y_sampling); % Recalculate coefficients

    % Forward phase
    forward_X_current = zeros(num_true, current_p + 1);
    for i = 1:current_p+1
        forward_X_current(:,i) = x_true .^ (i - 1);
    end
    forward_Y_current = forward_X_current * learn_C_current;

    average_error = mean(abs(forward_Y_current - y_true'));

    nexttile;
    plot(x_sampling, y_sampling, 'ro', x_true, y_true, 'b', x_true, forward_Y_current, 'g-');
    axis([min(x_true), max(x_true), min(y_true), max(y_true)]) % Scale axis to ground truth function
    title(sprintf('p = %d, mean error: %.2e', current_p, average_error));
    legend(sprintf('Samplings (%d)', num_samples), 'Ground truth', 'Prediction');
end

%% Definitions
function y = groundTruth(x)
    y = 1 - x.^2;
end
function y = sampleWithGaussianNoise(x, sigma)
    y = groundTruth(x) + sigma * randn(size(x));
end