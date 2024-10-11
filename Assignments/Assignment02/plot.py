import numpy as np
import matplotlib.pyplot as plt
from mpl_toolkits.mplot3d import Axes3D

# Rosenbrock function definition
def rosenbrock(X, Y):
    return 100 * (Y - X**2)**2 + (1 - X)**2

# Rastrigin function definition
def rastrigin(X, Y):
    A = 10
    return A * 2 + (X**2 - A * np.cos(2 * np.pi * X)) + (Y**2 - A * np.cos(2 * np.pi * Y))

def quadratic(X, Y):
    return X**2 + Y**2

# Function to extract data from the text file
def extract_data(file_path):
    x_values = []
    y_values = []
    objective_values = []
    function_name = None
    
    with open(file_path, 'r') as f:
        lines = f.readlines()
        
        for line in lines:
            if "Objective Function:" in line:
                function_name = line.split(":")[1].strip()
            if "x-values" in line:
                values = line.strip().split(":")[1].split()
                x_values.append(float(values[0]))
                y_values.append(float(values[1]))
            elif "Objective Function Value" in line:
                value = line.strip().split(":")[1]
                objective_values.append(float(value))
    
    return function_name, np.array(x_values), np.array(y_values), np.array(objective_values)

# Function to generate a 2D contour plot with arrows
def plot_2d_contour(x_values, y_values, function_name):
    # Determine the function and bounds
    if function_name == 'Rosenbrock':
        X, Y = np.meshgrid(np.linspace(-5, 5, 100), np.linspace(-5, 5, 100))
        Z = rosenbrock(X, Y)
    elif function_name == 'Rastrigin':
        X, Y = np.meshgrid(np.linspace(-5.12, 5.12, 100), np.linspace(-5.12, 5.12, 100))
        Z = rastrigin(X, Y)
    elif function_name == 'Quadratic':
        X, Y = np.meshgrid(np.linspace(-5, 5, 100), np.linspace(-5, 5, 100))
        Z = quadratic(X, Y)
    else:
        raise ValueError(f"Unknown function name: {function_name}")

    # Plot the contour
    plt.figure()
    contour = plt.contourf(X, Y, Z, levels=50, cmap='viridis')
    plt.colorbar(contour)
    
    # Draw arrows connecting the points
    plt.scatter(x_values, y_values, c='red', marker='x')  # Add points from the optimization process
    for i in range(len(x_values) - 1):
        plt.arrow(x_values[i], y_values[i],
                  x_values[i + 1] - x_values[i], y_values[i + 1] - y_values[i],
                  head_width=0.05, head_length=0.1, fc='blue', ec='blue')

    plt.xlabel('X')
    plt.ylabel('Y')
    plt.title(f'2D Contour Plot of {function_name} Function with Arrows')
    plt.show()

# Function to generate a 3D surface plot with arrows
def plot_3d_surface(x_values, y_values, objective_values, function_name):
    # Determine the function and bounds
    if function_name == 'Rosenbrock':
        X, Y = np.meshgrid(np.linspace(-5, 5, 100), np.linspace(-5, 5, 100))
        Z = rosenbrock(X, Y)
        Z_opt = rosenbrock(np.array(x_values), np.array(y_values))
    elif function_name == 'Rastrigin':
        X, Y = np.meshgrid(np.linspace(-5.12, 5.12, 100), np.linspace(-5.12, 5.12, 100))
        Z = rastrigin(X, Y)
        Z_opt = rastrigin(np.array(x_values), np.array(y_values))
    elif function_name == 'Quadratic':
        X, Y = np.meshgrid(np.linspace(-5, 5, 100), np.linspace(-5, 5, 100))
        Z = quadratic(X, Y)
        Z_opt = quadratic(np.array(x_values), np.array(y_values))
    else:
        raise ValueError(f"Unknown function name: {function_name}")

    # Plot the 3D surface
    fig = plt.figure()
    ax = fig.add_subplot(111, projection='3d')
    ax.plot_surface(X, Y, Z, cmap='viridis', edgecolor='none', alpha=0.8)
    
    # Add optimization points and connect them with arrows
    ax.scatter(x_values, y_values, Z_opt, color='red', marker='x')  # Plot optimization points
    #for i in range(len(x_values) - 1):
    #    ax.quiver(x_values[i], y_values[i], Z_opt[i],
    #              x_values[i+1] - x_values[i], y_values[i+1] - y_values[i], Z_opt[i+1] - Z_opt[i],
    #              color='blue', arrow_length_ratio=0.1)

    ax.set_xlabel('X')
    ax.set_ylabel('Y')
    ax.set_zlabel('Objective Function Value')
    ax.set_title(f'3D Surface Plot of {function_name} Function with Arrows')
    plt.show()

# Main function to run the script
def main():
    # Path to the text file
    file_path = 'output.txt'
    
    # Extract data from the file
    function_name, x_values, y_values, objective_values = extract_data(file_path)

    # Generate 2D contour plot with arrows
    plot_2d_contour(x_values, y_values, function_name)
    
    # Generate 3D surface plot with arrows
    plot_3d_surface(x_values, y_values, objective_values, function_name)

# Run the main function
if __name__ == "__main__":
    main()
