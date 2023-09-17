from flask import Flask, jsonify
from flask_sqlalchemy import SQLAlchemy

app = Flask(__name__)

# Configure the MySQL database connection
app.config['SQLALCHEMY_DATABASE_URI'] = 'mysql://username:password@localhost/database_name'
db = SQLAlchemy(app)

# Define an Employee model
class Employee(db.Model):
    id = db.Column(db.Integer, primary_key=True)
    name = db.Column(db.String(255))
    department = db.Column(db.String(255))

# Create the database tables
db.create_all()

# Define a route to retrieve all employees
@app.route('/employees', methods=['GET'])
def get_employees():
    employees = Employee.query.all()
    return jsonify([employee.__dict__ for employee in employees])

if __name__ == '__main__':
    app.run(debug=True)
