# The Wallet Service
A user, when associated with the Wallet service will get an access to the wallet. This wallet is a closed wallet in the
sense that user cannot add currency to the wallet on its own. This is an initial limitation and can be relaxed later. 
However, user can spend money at will on platforms that support this wallet.

As a User, you get to see your balance, last N transactions on the wallet and use wallet for payment.

As an Admin, you get to add balance to a given user, view wallet and view last N transactions of any given user. 
Admins also get to validate transaction balance of any user, by running through transactions logs. They may also 
request for correction, if validation indeed reports an error.

## Integrations
We will have two type of clients, one for whom we are maintaining the wallet, and second might want to use wallet as 
mode of payment. These can be same org, but will always be two clients for us.

This system will allow users to be integrated in via implementing user interface.
There will also an interface that client needs to implement if they want to transact with us.

All transactions will require two calls on our systems, one for fetching transactionId and other for the transaction.

The clients can always query back the status of the transaction.
Transactions can be reverted by the clients within N hours, post which transactions cannot be reverted.

However, clients can refund the transaction, partial or complete uptill N days. All refunds will be against 
transactionId of the original transaction. 
Also there will be a refundId(Another TransactionId) generated for each refunds, which can be used by 
reconciliation purposes.

