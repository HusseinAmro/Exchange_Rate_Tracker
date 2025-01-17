from datetime import datetime, timedelta
from flask import Flask, request, jsonify, abort
from flask_bcrypt import Bcrypt
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from flask_cors import CORS
import jwt
from .db_config import DB_CONFIG

app = Flask(__name__)
app.config['SQLALCHEMY_DATABASE_URI'] = DB_CONFIG
SECRET_KEY = "b'|\xe7\xbfU3`\xc4\xec\xa7\xa9zf:}\xb5\xc7\xb9\x139^3@Dv'"
CORS(app)
db = SQLAlchemy(app)
ma = Marshmallow(app)
bcrypt = Bcrypt(app)

from .model.user import User, user_schema
from .model.transaction import Transaction, transaction_schema, transactions_schema


@app.route('/transaction', methods=['POST'])
def create_transaction():
    usd = request.json['usd_amount']
    lbp = request.json['lbp_amount']
    usd_lbp = request.json['usd_to_lbp']

    auth_token = extract_auth_token(request)

    if auth_token:
        try:
            user_id = decode_token(auth_token)
        except (jwt.ExpiredSignatureError, jwt.InvalidTokenError):
            return jsonify({'message': 'Unauthorized'}), 403
        new_transaction = Transaction(usd_amount=usd, lbp_amount=lbp, usd_to_lbp=usd_lbp, user_id=user_id)
    else:
        new_transaction = Transaction(usd_amount=usd, lbp_amount=lbp, usd_to_lbp=usd_lbp)

    db.session.add(new_transaction)
    db.session.commit()

    response = {'message': 'Success', 'transaction': transaction_schema.dump(new_transaction)}
    return jsonify(response)


@app.route('/transaction', methods=['GET'])
def get_transactions():
    auth_token = extract_auth_token(request)

    if not auth_token:
        return jsonify({'message': 'Unauthorized'}), 403

    try:
        user_id = decode_token(auth_token)
    except (jwt.ExpiredSignatureError, jwt.InvalidTokenError):
        return jsonify({'message': 'Unauthorized'}), 403

    transactions = Transaction.query.filter_by(user_id=user_id).all()
    response = {'transactions': transactions_schema.dump(transactions)}
    return jsonify(response)


@app.route('/transactionAndr', methods=['GET'])
def get_transactionsAndr():
    auth_token = extract_auth_token(request)
    if auth_token:
        try:
            user_id = decode_token(auth_token)
        except:
            return jsonify({'message': 'Unauthorized'}), 403
    else:
        return jsonify({'message': 'Unauthorized'}), 403
    transactions = Transaction.query.filter_by(user_id=user_id).all()
    return jsonify(transactions_schema.dump(transactions))


@app.route('/exchangeRate', methods=['GET'])
def exchange_rate():
    start_date = datetime.now() - timedelta(hours=72)
    usd_lbp_tran = Transaction.query.filter(
        Transaction.added_date.between(start_date, datetime.now()),
        Transaction.usd_to_lbp == 1).all()
    lbp_usd_tran = Transaction.query.filter(
        Transaction.added_date.between(start_date, datetime.now()),
        Transaction.usd_to_lbp == 0).all()

    usd_lbp_av = "Not Yet Available"
    lbp_usd_av = "Not Yet Available"
    usd_lbp_total = 0
    lbp_usd_total = 0
    usd_lbp_count = 0
    lbp_usd_count = 0

    for transaction in usd_lbp_tran:
        if transaction.usd_amount > 0:
            usd_lbp_count += 1
            usd_lbp_total += transaction.lbp_amount / transaction.usd_amount

    for transaction in lbp_usd_tran:
        if transaction.usd_amount > 0:
            lbp_usd_count += 1
            lbp_usd_total += transaction.lbp_amount / transaction.usd_amount

    if usd_lbp_count != 0:
        usd_lbp_av = usd_lbp_total / usd_lbp_count
    if lbp_usd_count != 0:
        lbp_usd_av = lbp_usd_total / lbp_usd_count

    exchange_rates = {
        "usd_to_lbp": usd_lbp_av,
        "lbp_to_usd": lbp_usd_av
    }

    return jsonify(exchange_rates)


@app.route('/user', methods=['POST'])
def create_user():
    user_name = request.json['user_name']
    password = request.json['password']

    existing_user = User.query.filter_by(user_name=user_name).first()
    if existing_user:
        return jsonify({'message': 'User already exists'}), 400

    new_user = User(user_name=user_name, password=password)
    db.session.add(new_user)
    db.session.commit()

    response = {'message': 'Success', 'user': user_schema.dump(new_user)}
    return jsonify(response)


@app.route('/authentication', methods=['POST'])
def auth_user():
    user_name = request.json.get('user_name')
    password = request.json.get('password')

    if not user_name or not password:
        abort(400)

    user = User.query.filter_by(user_name=user_name).first()
    if not user:
        abort(403)

    if bcrypt.check_password_hash(user.hashed_password, password):
        token = create_token(user.id)
        return jsonify({'token': token}), 200
    else:
        abort(403)


def create_token(user_id):
    time_now = datetime.utcnow()
    payload = {
        'exp': time_now + timedelta(days=4),
        'iat': time_now,
        'sub': user_id
    }
    return jwt.encode(
        payload,
        SECRET_KEY,
        algorithm='HS256'
    )


def extract_auth_token(authenticated_request):
    auth_header = authenticated_request.headers.get('Authorization')
    if auth_header:
        return auth_header.split(" ")[1]
    else:
        return None


def decode_token(token):
    payload = jwt.decode(token, SECRET_KEY, 'HS256')
    return payload['sub']


if __name__ == '__main__':
    with app.app_context():
        db.create_all()
    app.run()
