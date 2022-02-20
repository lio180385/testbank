# testbank


#endpoint
generated Token
localhost:8089/authenticate
#Create  User Param token
localhost:8089/api/nasabah/register
#Save ManyTo Many  Type by User
localhost:8089/api/transaction/saveUserType
#Create Transaction
localhost:8089/api/transaction/saveTransaction
#Check Mutasi
localhost:8089/api/transaction/checkTransaction
#Generate exel Mutasi
http://localhost:8089/api/transaction/download/excel?startdate=19-02-2022&enddate=20-02-2022&User=Leo
