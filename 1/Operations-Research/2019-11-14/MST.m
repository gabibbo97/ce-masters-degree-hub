%% Cleanup
clear
clc
close all


%% Graph definition
G = [
    0 2 5 6 0 0 0 0; ... % three dots because MATLAB
    2 0 5 3 4 0 0 0; ...
    5 5 0 1 0 6 0 3; ...
    6 3 1 0 2 3 3 5; ...
    0 4 0 2 0 0 1 0; ...
    0 0 6 3 0 0 2 3; ...
    0 0 0 3 1 2 0 4; ...
    0 0 3 5 0 3 4 0; ...
];
node_names = {'A' 'B' 'C' 'D' 'E' 'F' 'G' 'H'}; % Node aliases
graph = graph(G, node_names); % Build an indirected graph

%% Calculate minimum spanning tree
min_spanning_tree_default = minspantree(graph);
min_spanning_tree_dijikstra = minspantree(graph,'Method','dense');

%% Show the graph
tiledlayout(1,3) % Arrange plots in a grid

nexttile;
plot(graph);
title('Original graph');

nexttile;
plot(min_spanning_tree_default);
title('MST (Default)');

nexttile;
plot(min_spanning_tree_dijikstra);
title('MST (Dense)');